package com.coreo.listbox.repository

import app.cash.sqldelight.coroutines.asFlow
import com.coreo.listbox.database.FieldDefinitionEntity
import com.coreo.listbox.database.FieldOptionEntity
import com.coreo.listbox.database.FieldValueEntity
import com.coreo.listbox.database.ListBoxDatabase
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.util.getCurrentTimestampMillis
import com.coreo.listbox.util.generateUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class ListBoxRepository(private val database: ListBoxDatabase) {
    
    /**
     * Get all lists from the database as a reactive Flow, sorted by creation date (descending)
     */
    fun getAllLists(): Flow<List<ListEntity>> {
        return database.listEntityQueries.getAllLists()
            .asFlow()
            .map { it.executeAsList() }
    }
    
    /**
     * Get a specific list by ID as a reactive Flow
     */
    fun getListById(listId: String): Flow<ListEntity?> {
        return database.listEntityQueries.getListById(listId)
            .asFlow()
            .map { it.executeAsOneOrNull() }
    }
    
    /**
     * Create a new list with an auto-generated UUID and return the created entity
     */
    suspend fun createList(title: String): ListEntity {
        val id = generateUUID()
        val now = getCurrentTimestampMillis()
        database.listEntityQueries.insertList(
            id = id,
            title = title,
            createdAt = now,
            updatedAt = now
        )
        return ListEntity(
            id = id,
            title = title,
            createdAt = now,
            updatedAt = now
        )
    }
    
    /**
     * Update a list title
     */
    suspend fun updateListTitle(listId: String, newTitle: String) {
        val now = getCurrentTimestampMillis()
        database.listEntityQueries.updateListTitle(
            title = newTitle,
            updatedAt = now,
            id = listId
        )
    }
    
    /**
     * Delete a list and all its items
     */
    suspend fun deleteList(listId: String) {
        database.listEntityQueries.deleteList(id = listId)
    }
    
    /**
     * Create a new item with an auto-generated UUID, appended to the end of the list
     */
    suspend fun createItem(listId: String, title: String, description: String?) {
        val id = generateUUID()
        val maxOrderIndex = database.itemEntityQueries.getMaxOrderIndex(listId)
            .executeAsOneOrNull()
            ?.MAX
            ?: 0L
        val newOrderIndex = maxOrderIndex + 1L
        database.itemEntityQueries.insertItem(
            id = id,
            listId = listId,
            title = title,
            description = description,
            orderIndex = newOrderIndex
        )
    }

    /**
     * Update the orderIndex of an item for drag-and-drop reordering
     */
    suspend fun reorderItem(itemId: String, newOrderIndex: Long) {
        database.itemEntityQueries.updateItemOrderIndex(
            orderIndex = newOrderIndex,
            id = itemId
        )
    }

    /**
     * Batch update orderIndex for multiple items in a single transaction
     */
    suspend fun reorderItems(orderUpdates: List<Pair<String, Long>>) {
        if (orderUpdates.isEmpty()) return
        database.transaction {
            orderUpdates.forEach { (itemId, newOrderIndex) ->
                database.itemEntityQueries.updateItemOrderIndex(
                    orderIndex = newOrderIndex,
                    id = itemId
                )
            }
        }
    }

    /**
     * Get all items for a specific list as a reactive Flow, sorted by orderIndex
     */
    fun getItemsForList(listId: String): Flow<List<ItemEntity>> {
        return database.itemEntityQueries.getItemsByListId(listId)
            .asFlow()
            .map { it.executeAsList() }
    }

    /**
     * Get a specific item by ID as a reactive Flow
     */
    fun getItemById(itemId: String): Flow<ItemEntity?> {
        return database.itemEntityQueries.getItemById(itemId)
            .asFlow()
            .map { it.executeAsOneOrNull() }
    }

    /**
     * Update an item's title and description
     */
    suspend fun updateItem(itemId: String, title: String, description: String?) {
        database.itemEntityQueries.updateItem(
            title = title,
            description = description,
            id = itemId
        )
    }

    /**
     * Delete a single item by ID
     */
    suspend fun deleteItem(itemId: String) {
        database.itemEntityQueries.deleteItem(id = itemId)
    }

    /**
     * Delete multiple items in a single query
     */
    suspend fun deleteItems(itemIds: Collection<String>) {
        database.itemEntityQueries.deleteItemsByIds(id = itemIds)
    }

    // ---- Custom Field Definitions ----

    /**
     * Get all field definitions for a list as a reactive Flow, ordered by orderIndex
     */
    fun getFieldDefinitionsForList(listId: String): Flow<List<FieldDefinitionEntity>> {
        return database.fieldDefinitionEntityQueries.getFieldDefinitionsByListId(listId)
            .asFlow()
            .map { it.executeAsList() }
    }

    /**
     * Create a new field definition for a list, along with any initial dropdown options
     */
    suspend fun createFieldDefinition(
        listId: String,
        name: String,
        dataType: String,
        options: List<String> = emptyList()
    ) {
        val fieldId = generateUUID()
        val maxOrderIndex = database.fieldDefinitionEntityQueries
            .getMaxFieldOrderIndex(listId)
            .executeAsOneOrNull()
            ?.MAX ?: 0L
        database.fieldDefinitionEntityQueries.insertFieldDefinition(
            id = fieldId,
            listId = listId,
            name = name,
            dataType = dataType,
            orderIndex = maxOrderIndex + 1L
        )
        options.forEachIndexed { index, label ->
            database.fieldOptionEntityQueries.insertFieldOption(
                id = generateUUID(),
                fieldDefinitionId = fieldId,
                label = label,
                orderIndex = index.toLong()
            )
        }
    }

    /**
     * Delete a field definition and cascade its options and values
     */
    suspend fun deleteFieldDefinition(fieldDefinitionId: String) {
        database.transaction {
            database.fieldValueEntityQueries.deleteFieldValuesByFieldDefinitionId(fieldDefinitionId)
            database.fieldOptionEntityQueries.deleteFieldOptionsByDefinitionId(fieldDefinitionId)
            database.fieldDefinitionEntityQueries.deleteFieldDefinition(fieldDefinitionId)
        }
    }

    // ---- Field Options ----

    /**
     * Get all options for a dropdown field definition as a reactive Flow
     */
    fun getFieldOptionsForDefinition(fieldDefinitionId: String): Flow<List<FieldOptionEntity>> {
        return database.fieldOptionEntityQueries
            .getFieldOptionsByDefinitionId(fieldDefinitionId)
            .asFlow()
            .map { it.executeAsList() }
    }

    // ---- Field Values ----

    /**
     * Get all field values for an item as a reactive Flow
     */
    fun getFieldValuesForItem(itemId: String): Flow<List<FieldValueEntity>> {
        return database.fieldValueEntityQueries.getFieldValuesByItemId(itemId)
            .asFlow()
            .map { it.executeAsList() }
    }

    /**
     * Upsert a field value for a specific item + field definition pair
     */
    suspend fun upsertFieldValue(itemId: String, fieldDefinitionId: String, value: String) {
        val existing = database.fieldValueEntityQueries
            .getFieldValueByItemAndDefinition(itemId, fieldDefinitionId)
            .executeAsOneOrNull()
        val id = existing?.id ?: generateUUID()
        database.fieldValueEntityQueries.upsertFieldValue(
            id = id,
            itemId = itemId,
            fieldDefinitionId = fieldDefinitionId,
            value_ = value
        )
    }
}
