package com.coreo.listbox.repository

import com.coreo.listbox.database.ListBoxDatabase
import com.coreo.listbox.util.getCurrentTimestampMillis
import me.tatarka.inject.annotations.Inject

@Inject
class ListBoxRepository(private val database: ListBoxDatabase) {
    
    /**
     * Get all lists from the database, sorted by creation date (descending)
     */
    fun getAllLists() = database.listEntityQueries.getAllLists()
    
    /**
     * Get a specific list by ID
     */
    fun getListById(listId: String) = database.listEntityQueries.getListById(listId)
    
    /**
     * Create a new list
     */
    fun createList(id: String, title: String, createdAt: Long) {
        val now = getCurrentTimestampMillis()
        database.listEntityQueries.insertList(
            id = id,
            title = title,
            createdAt = createdAt,
            updatedAt = now
        )
    }
    
    /**
     * Update a list title
     */
    fun updateListTitle(listId: String, newTitle: String) {
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
    fun deleteList(listId: String) {
        database.listEntityQueries.deleteList(id = listId)
    }
    
    /**
     * Get all items in a list
     */
    fun getItemsByListId(listId: String) = database.itemEntityQueries.getItemsByListId(listId)
    
    /**
     * Get a specific item by ID
     */
    fun getItemById(itemId: String) = database.itemEntityQueries.getItemById(itemId)
    
    /**
     * Create a new item
     */
    fun createItem(
        id: String,
        listId: String,
        title: String,
        description: String?,
        position: Long
    ) {
        database.itemEntityQueries.insertItem(
            id = id,
            listId = listId,
            title = title,
            description = description,
            position = position
        )
    }
    
    /**
     * Update an item
     */
    fun updateItem(
        itemId: String,
        title: String,
        description: String?
    ) {
        database.itemEntityQueries.updateItem(
            title = title,
            description = description,
            id = itemId
        )
    }
    
    /**
     * Update item position (for reordering)
     */
    fun updateItemPosition(itemId: String, position: Long) {
        database.itemEntityQueries.updateItemPosition(
            position = position,
            id = itemId
        )
    }
    
    /**
     * Delete a single item
     */
    fun deleteItem(itemId: String) {
        database.itemEntityQueries.deleteItem(id = itemId)
    }
    
    /**
     * Delete all items in a list
     */
    fun deleteItemsByListId(listId: String) {
        database.itemEntityQueries.deleteItemsByListId(listId = listId)
    }
}
