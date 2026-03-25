package com.coreo.listbox.repository

import app.cash.sqldelight.coroutines.asFlow
import com.coreo.listbox.database.ListBoxDatabase
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.util.getCurrentTimestampMillis
import com.coreo.listbox.util.generateUUID
import kotlinx.coroutines.Dispatchers
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
     * Create a new item with an auto-generated UUID
     */
    suspend fun createItem(listId: String, title: String, description: String?, position: Int = 0) {
        val id = generateUUID()
        database.itemEntityQueries.insertItem(
            id = id,
            listId = listId,
            title = title,
            description = description,
            position = position.toLong()
        )
    }
    
    /**
     * Get all items for a specific list as a reactive Flow, sorted by position
     */
    fun getItemsForList(listId: String): Flow<List<ItemEntity>> {
        return database.itemEntityQueries.getItemsByListId(listId)
            .asFlow()
            .map { it.executeAsList() }
    }
}
