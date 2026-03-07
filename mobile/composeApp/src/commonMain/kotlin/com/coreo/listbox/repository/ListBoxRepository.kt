package com.coreo.listbox.repository

import app.cash.sqldelight.coroutines.asFlow
import com.coreo.listbox.database.ListBoxDatabase
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
}
