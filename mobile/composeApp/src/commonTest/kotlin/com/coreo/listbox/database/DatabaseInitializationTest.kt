package com.coreo.listbox.database

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DatabaseInitializationTest {
    @Test
    fun testDatabaseProviderInitialization() {
        // Initialize the database
        DatabaseProvider.initialize()
        
        // Get the database instance
        val database = DatabaseProvider.getDatabase()
        assertNotNull(database, "Database should not be null after initialization")
    }
    
    @Test
    fun testDatabaseMultipleInitialization() {
        // Initialize twice to test singleton pattern
        DatabaseProvider.initialize()
        val firstDb = DatabaseProvider.getDatabase()
        
        DatabaseProvider.initialize()
        val secondDb = DatabaseProvider.getDatabase()
        
        // Should be the same instance
        assertTrue(firstDb === secondDb, "Multiple initializations should return the same database instance")
    }
}
