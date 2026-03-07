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
        // Sample data removed - starting with empty database
        sampleDataAdded = true
    }
}
