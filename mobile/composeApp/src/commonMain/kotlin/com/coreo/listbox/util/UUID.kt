package com.coreo.listbox.util

/**
 * Generate a UUID v4 string using platform-native implementations
 * Android/JVM: java.util.UUID.randomUUID()
 * iOS: NSUUID
 */
expect fun generateUUID(): String
