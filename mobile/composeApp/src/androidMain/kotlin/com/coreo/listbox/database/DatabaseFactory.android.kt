package com.coreo.listbox.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.coreo.listbox.database.ListBoxDatabase

object DatabaseContext {
    lateinit var appContext: Context
}

actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = ListBoxDatabase.Schema,
        context = DatabaseContext.appContext,
        name = "listbox.db"
    )
}
