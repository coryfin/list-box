package com.coreo.listbox.model

/**
 * Represents a single option in a dropdown custom field, pairing a display label with
 * a color hex string from the predefined palette (e.g. "#C6E5FF").
 */
data class DropdownOption(
    val label: String,
    val color: String
)
