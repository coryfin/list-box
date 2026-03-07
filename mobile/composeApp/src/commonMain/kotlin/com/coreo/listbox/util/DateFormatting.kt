package com.coreo.listbox.util

/**
 * Format a timestamp in milliseconds to a human-readable date string for display in list cards.
 * Platform-specific implementation required.
 * Example: "Jan 15, 2025"
 */
expect fun formatDateForListCard(timestampMillis: Long): String

/**
 * Format a timestamp in milliseconds to a full readable format for display in detail views.
 * Platform-specific implementation required.
 * Example: "January 15, 2025 at 2:34 PM"
 */
expect fun formatDateFull(timestampMillis: Long): String
