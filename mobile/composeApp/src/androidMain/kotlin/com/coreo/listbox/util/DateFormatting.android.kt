package com.coreo.listbox.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatDateForListCard(timestampMillis: Long): String {
    val date = Date(timestampMillis)
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.US)
    return formatter.format(date)
}

actual fun formatDateFull(timestampMillis: Long): String {
    val date = Date(timestampMillis)
    val formatter = SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.US)
    return formatter.format(date)
}
