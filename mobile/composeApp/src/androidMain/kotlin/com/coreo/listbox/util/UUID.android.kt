package com.coreo.listbox.util

import java.util.UUID

actual fun generateUUID(): String = UUID.randomUUID().toString()
