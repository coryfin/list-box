package com.coreo.listbox.database

import app.cash.sqldelight.db.SqlDriver

object DatabaseProvider {
    private lateinit var database: ListBoxDatabase
    private var isInitialized = false

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
}
