package com.coreo.listbox.repository

import app.cash.sqldelight.coroutines.asFlow
import com.coreo.listbox.database.FieldDefinitionEntity
import com.coreo.listbox.database.FieldOptionEntity
import com.coreo.listbox.database.FieldValueEntity
import com.coreo.listbox.database.GetItemsWithVisibleFieldValues
import com.coreo.listbox.database.ListBoxDatabase
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.util.getCurrentTimestampMillis
import com.coreo.listbox.util.generateUUID
import com.coreo.listbox.model.DropdownOption
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
        val maxOrderIndex = database.listEntityQueries.getMaxListOrderIndex()
            .executeAsOneOrNull()
            ?.MAX ?: 0L
        val newOrderIndex = maxOrderIndex + 1L
        database.listEntityQueries.insertList(
            id = id,
            title = title,
            createdAt = now,
            updatedAt = now,
            orderIndex = newOrderIndex
        )
        return ListEntity(
            id = id,
            title = title,
            createdAt = now,
            updatedAt = now,
            orderIndex = newOrderIndex
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
     * Deep-copy a list with a new title. Copies items, field definitions, field options,
     * and field values in a single transaction. Returns the new list's ID.
     */
    fun copyList(sourceListId: String, newTitle: String): String {
        val newListId = generateUUID()
        val now = getCurrentTimestampMillis()

        val sourceItems = database.itemEntityQueries
            .getItemsByListId(sourceListId).executeAsList()
        val sourceFieldDefs = database.fieldDefinitionEntityQueries
            .getFieldDefinitionsByListId(sourceListId).executeAsList()

        // oldItemId -> newItemId
        val itemIdMap = sourceItems.associate { it.id to generateUUID() }
        // oldFieldDefId -> newFieldDefId
        val fieldDefIdMap = sourceFieldDefs.associate { it.id to generateUUID() }

        database.transaction {
            val maxOrderIndex = database.listEntityQueries.getMaxListOrderIndex()
                .executeAsOneOrNull()
                ?.MAX ?: 0L
            database.listEntityQueries.insertList(
                id = newListId,
                title = newTitle,
                createdAt = now,
                updatedAt = now,
                orderIndex = maxOrderIndex + 1L
            )

            for (item in sourceItems) {
                database.itemEntityQueries.insertItem(
                    id = itemIdMap.getValue(item.id),
                    listId = newListId,
                    title = item.title,
                    description = item.description,
                    orderIndex = item.orderIndex
                )
            }

            for (fieldDef in sourceFieldDefs) {
                val newFieldDefId = fieldDefIdMap.getValue(fieldDef.id)
                database.fieldDefinitionEntityQueries.insertFieldDefinition(
                    id = newFieldDefId,
                    listId = newListId,
                    name = fieldDef.name,
                    dataType = fieldDef.dataType,
                    orderIndex = fieldDef.orderIndex
                )
                if (fieldDef.visible == 1L) {
                    database.fieldDefinitionEntityQueries.updateFieldVisibility(
                        visible = 1L,
                        id = newFieldDefId
                    )
                }

                val options = database.fieldOptionEntityQueries
                    .getFieldOptionsByDefinitionId(fieldDef.id).executeAsList()
                for (option in options) {
                    database.fieldOptionEntityQueries.insertFieldOption(
                        id = generateUUID(),
                        fieldDefinitionId = newFieldDefId,
                        label = option.label,
                        orderIndex = option.orderIndex,
                        color = option.color
                    )
                }
            }

            for (item in sourceItems) {
                val fieldValues = database.fieldValueEntityQueries
                    .getFieldValuesByItemId(item.id).executeAsList()
                for (fv in fieldValues) {
                    val newItemId = itemIdMap.getValue(item.id)
                    val newFieldDefId = fieldDefIdMap[fv.fieldDefinitionId] ?: continue
                    database.fieldValueEntityQueries.upsertFieldValue(
                        id = generateUUID(),
                        itemId = newItemId,
                        fieldDefinitionId = newFieldDefId,
                        value_ = fv.value_
                    )
                }
            }
        }

        return newListId
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
     * Batch update orderIndex for multiple lists in a single transaction
     */
    suspend fun reorderLists(orderUpdates: List<Pair<String, Long>>) {
        if (orderUpdates.isEmpty()) return
        database.transaction {
            orderUpdates.forEach { (listId, newOrderIndex) ->
                database.listEntityQueries.updateListOrderIndex(
                    orderIndex = newOrderIndex,
                    id = listId
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
     * Get all (item, visible field definition, field value) rows for a list as a reactive Flow.
     * Returns one row per (item × visible field definition) pair; fieldValue is null when not set.
     */
    fun getItemsWithVisibleFieldValues(listId: String): Flow<List<GetItemsWithVisibleFieldValues>> {
        return database.itemEntityQueries.getItemsWithVisibleFieldValues(listId, listId)
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
        options: List<DropdownOption> = emptyList()
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
        options.forEachIndexed { index, option ->
            database.fieldOptionEntityQueries.insertFieldOption(
                id = generateUUID(),
                fieldDefinitionId = fieldId,
                label = option.label,
                orderIndex = index.toLong(),
                color = option.color
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

    /**
     * Update a field definition's name and data type, replacing all options atomically
     */
    suspend fun updateFieldDefinition(
        fieldId: String,
        name: String,
        dataType: String,
        options: List<DropdownOption> = emptyList()
    ) {
        database.transaction {
            database.fieldDefinitionEntityQueries.updateFieldDefinition(
                name = name,
                dataType = dataType,
                id = fieldId
            )
            database.fieldOptionEntityQueries.deleteFieldOptionsByDefinitionId(fieldId)
            options.forEachIndexed { index, option ->
                database.fieldOptionEntityQueries.insertFieldOption(
                    id = generateUUID(),
                    fieldDefinitionId = fieldId,
                    label = option.label,
                    orderIndex = index.toLong(),
                    color = option.color
                )
            }
        }
    }

    /**
     * Toggle the visibility of a custom field
     */
    suspend fun updateFieldDefinitionVisibility(fieldDefinitionId: String, visible: Boolean) {
        database.fieldDefinitionEntityQueries.updateFieldVisibility(
            visible = if (visible) 1L else 0L,
            id = fieldDefinitionId
        )
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
     * Move a collection of items to a destination list.
     *
     * For each [sourceFieldDefIdsToCreate], a new field definition (with its options) is created
     * in the destination list. Field values for all moved items are carried over using:
     *   - Newly created field defs (from [sourceFieldDefIdsToCreate])
     *   - Existing destination field defs whose label matches a source field def label (auto-carry)
     *
     * All operations run in a single transaction.
     */
    suspend fun moveItems(
        itemIds: Collection<String>,
        destinationListId: String,
        sourceFieldDefIdsToCreate: List<String>
    ) {
        if (itemIds.isEmpty()) return

        val sourceFieldDefs = sourceFieldDefIdsToCreate.mapNotNull { id ->
            database.fieldDefinitionEntityQueries.getFieldDefinitionById(id).executeAsOneOrNull()
        }
        val destFieldDefs = database.fieldDefinitionEntityQueries
            .getFieldDefinitionsByListId(destinationListId).executeAsList()
        val destFieldDefsByLabel = destFieldDefs.associateBy { it.name }

        database.transaction {
            // Build source→dest field def ID mapping for newly created fields
            val sourceToDestFieldDefId = mutableMapOf<String, String>()

            for (fieldDef in sourceFieldDefs) {
                val newFieldDefId = generateUUID()
                val maxOrderIndex = database.fieldDefinitionEntityQueries
                    .getMaxFieldOrderIndex(destinationListId).executeAsOneOrNull()?.MAX ?: 0L
                database.fieldDefinitionEntityQueries.insertFieldDefinition(
                    id = newFieldDefId,
                    listId = destinationListId,
                    name = fieldDef.name,
                    dataType = fieldDef.dataType,
                    orderIndex = maxOrderIndex + 1L
                )
                val options = database.fieldOptionEntityQueries
                    .getFieldOptionsByDefinitionId(fieldDef.id).executeAsList()
                for (option in options) {
                    database.fieldOptionEntityQueries.insertFieldOption(
                        id = generateUUID(),
                        fieldDefinitionId = newFieldDefId,
                        label = option.label,
                        orderIndex = option.orderIndex,
                        color = option.color
                    )
                }
                sourceToDestFieldDefId[fieldDef.id] = newFieldDefId
            }

            // Add auto-carry mappings: source fields whose label matches an existing dest field
            val allSourceFieldDefs = database.fieldDefinitionEntityQueries
                .getFieldDefinitionsByListId(
                    // Derive sourceListId from any of the items being moved
                    database.itemEntityQueries.getItemById(itemIds.first())
                        .executeAsOneOrNull()?.listId ?: return@transaction
                ).executeAsList()
            for (srcDef in allSourceFieldDefs) {
                if (!sourceToDestFieldDefId.containsKey(srcDef.id)) {
                    val matchingDestDef = destFieldDefsByLabel[srcDef.name]
                    if (matchingDestDef != null) {
                        sourceToDestFieldDefId[srcDef.id] = matchingDestDef.id
                    }
                }
            }

            // Move each item
            for (itemId in itemIds) {
                val maxOrderIndex = database.itemEntityQueries
                    .getMaxOrderIndex(destinationListId).executeAsOneOrNull()?.MAX ?: 0L
                database.itemEntityQueries.moveItemToList(
                    listId = destinationListId,
                    orderIndex = maxOrderIndex + 1L,
                    id = itemId
                )

                // Carry over field values
                val fieldValues = database.fieldValueEntityQueries
                    .getFieldValuesByItemId(itemId).executeAsList()
                for (fv in fieldValues) {
                    val destFieldDefId = sourceToDestFieldDefId[fv.fieldDefinitionId] ?: continue
                    database.fieldValueEntityQueries.upsertFieldValue(
                        id = generateUUID(),
                        itemId = itemId,
                        fieldDefinitionId = destFieldDefId,
                        value_ = fv.value_
                    )
                }
            }
        }
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
