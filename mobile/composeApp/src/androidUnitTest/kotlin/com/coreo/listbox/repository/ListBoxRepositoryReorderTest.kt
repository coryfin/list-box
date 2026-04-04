package com.coreo.listbox.repository

import app.cash.sqldelight.Query
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import com.coreo.listbox.database.ListBoxDatabase
import kotlinx.coroutines.test.runTest
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals

private class InMemoryDriver : JdbcDriver() {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
    private val listeners = mutableMapOf<String, MutableSet<Query.Listener>>()

    override fun getConnection(): Connection = connection
    override fun closeConnection(connection: Connection) {} // keep alive for in-memory db

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        synchronized(listeners) {
            queryKeys.forEach { listeners.getOrPut(it) { mutableSetOf() }.add(listener) }
        }
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        synchronized(listeners) {
            queryKeys.forEach { listeners[it]?.remove(listener) }
        }
    }

    override fun notifyListeners(vararg queryKeys: String) {
        val toNotify = synchronized(listeners) {
            queryKeys.flatMap { listeners[it]?.toList() ?: emptyList() }.toSet()
        }
        toNotify.forEach(Query.Listener::queryResultsChanged)
    }
}

class ListBoxRepositoryReorderTest {

    private fun createTestEnvironment(): Pair<ListBoxRepository, ListBoxDatabase> {
        val driver = InMemoryDriver()
        ListBoxDatabase.Schema.create(driver)
        val database = ListBoxDatabase(driver)
        return ListBoxRepository(database) to database
    }

    @Test
    fun reorderItem_updatesOrderIndex() = runTest {
        val (repository, database) = createTestEnvironment()

        val list = repository.createList("Test List")
        repository.createItem(list.id, "Item A", null)
        repository.createItem(list.id, "Item B", null)

        val items = database.itemEntityQueries.getItemsByListId(list.id).executeAsList()
        val itemB = items.first { it.title == "Item B" }

        // Move Item B before Item A using a lower orderIndex
        repository.reorderItem(itemB.id, 0L)

        val reordered = database.itemEntityQueries.getItemsByListId(list.id).executeAsList()
        assertEquals("Item B", reordered[0].title)
        assertEquals("Item A", reordered[1].title)
        assertEquals(0L, reordered[0].orderIndex)
    }

    @Test
    fun reorderItem_movesItemBetweenOthers() = runTest {
        val (repository, database) = createTestEnvironment()

        val list = repository.createList("Test List")
        repository.createItem(list.id, "Item A", null)
        repository.createItem(list.id, "Item B", null)
        repository.createItem(list.id, "Item C", null)

        val items = database.itemEntityQueries.getItemsByListId(list.id).executeAsList()
        // Items: A(1L), B(2L), C(3L). Move C between A and B using index 1
        val itemC = items.first { it.title == "Item C" }
        repository.reorderItem(itemC.id, 1L)

        val reordered = database.itemEntityQueries.getItemsByListId(list.id).executeAsList()
        assertEquals("Item A", reordered[0].title)
        assertEquals("Item C", reordered[1].title)
        assertEquals("Item B", reordered[2].title)
    }

    @Test
    fun reorderItem_doesNotAffectOtherItems() = runTest {
        val (repository, database) = createTestEnvironment()

        val list = repository.createList("Test List")
        repository.createItem(list.id, "Item A", null)
        repository.createItem(list.id, "Item B", null)

        val items = database.itemEntityQueries.getItemsByListId(list.id).executeAsList()
        val itemA = items.first { it.title == "Item A" }
        val originalIndexB = items.first { it.title == "Item B" }.orderIndex

        repository.reorderItem(itemA.id, 5L)

        val updated = database.itemEntityQueries.getItemsByListId(list.id).executeAsList()
        assertEquals(originalIndexB, updated.first { it.title == "Item B" }.orderIndex)
    }
}
