package com.coreo.listbox.database

import app.cash.sqldelight.db.SqlDriver
import com.coreo.listbox.util.getCurrentTimestampMillis
import com.coreo.listbox.util.generateUUID

object DatabaseProvider {
    private lateinit var database: ListBoxDatabase
    private var isInitialized = false
    private var sampleDataAdded = false

    fun initialize() {
        if (!isInitialized) {
            val driver = createDriver()
            database = ListBoxDatabase(driver)
            isInitialized = true
        }
    }

    fun getDatabase(): ListBoxDatabase {
        if (!isInitialized) {
            initialize()
        }
        return database
    }
    
    fun addSampleData() {
        if (!sampleDataAdded && isInitialized) {
            val now = getCurrentTimestampMillis()
            
            // Add three sample lists for testing
            database.listEntityQueries.insertList(
                id = generateUUID(),
                title = "Gift Ideas",
                createdAt = now - 86400000 * 3, // 3 days ago
                updatedAt = now - 86400000 * 3
            )
            
            database.listEntityQueries.insertList(
                id = generateUUID(),
                title = "Recipe Box",
                createdAt = now - 86400000 * 2, // 2 days ago
                updatedAt = now - 86400000 * 2
            )
            
            database.listEntityQueries.insertList(
                id = generateUUID(),
                title = "Personal Goals",
                createdAt = now - 86400000, // 1 day ago
                updatedAt = now - 86400000
            )
            
            sampleDataAdded = true
        }
    }
}
