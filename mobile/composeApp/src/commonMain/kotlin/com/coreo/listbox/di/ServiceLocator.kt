package com.coreo.listbox.di

import com.coreo.listbox.database.ListBoxDatabase
import com.coreo.listbox.database.DatabaseProvider
import com.coreo.listbox.repository.ListBoxRepository

object ServiceLocator {
    private var database: ListBoxDatabase? = null
    private var repository: ListBoxRepository? = null
    
    fun getDatabase(): ListBoxDatabase {
        if (database == null) {
            database = DatabaseProvider.getDatabase()
            // Add sample data for testing
            DatabaseProvider.addSampleData()
        }
        return database!!
    }
    
    fun getRepository(): ListBoxRepository {
        if (repository == null) {
            repository = ListBoxRepository(getDatabase())
        }
        return repository!!
    }
}
