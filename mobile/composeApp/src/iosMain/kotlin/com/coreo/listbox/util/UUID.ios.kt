package com.coreo.listbox.util

import platform.Foundation.NSUUID

actual fun generateUUID(): String = NSUUID().UUIDString()
