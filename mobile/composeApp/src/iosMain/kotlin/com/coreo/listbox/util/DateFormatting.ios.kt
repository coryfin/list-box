package com.coreo.listbox.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual fun formatDateForListCard(timestampMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestampMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    
    val month = months[localDateTime.monthNumber - 1]
    val day = localDateTime.dayOfMonth
    val year = localDateTime.year
    
    return "$month $day, $year"
}

actual fun formatDateFull(timestampMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestampMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val fullMonths = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    
    val month = fullMonths[localDateTime.monthNumber - 1]
    val day = localDateTime.dayOfMonth
    val year = localDateTime.year
    val hour = localDateTime.hour
    val minute = localDateTime.minute.toString().padStart(2, '0')
    
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    
    return "$month $day, $year at $displayHour:$minute $amPm"
}
