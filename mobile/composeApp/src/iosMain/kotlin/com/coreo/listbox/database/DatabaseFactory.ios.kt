package com.coreo.listbox.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = ListBoxDatabase.Schema,
        name = "listbox.db"
    )
}
