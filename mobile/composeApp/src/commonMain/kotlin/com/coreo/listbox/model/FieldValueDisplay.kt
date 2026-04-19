package com.coreo.listbox.model

/**
 * A field name + value pair ready for display, with an optional color hint.
 * [optionColor] is the containerHex from the palette when the field is a DROPDOWN with a
 * matching option; null otherwise (TEXT fields, or a dropdown with no value set).
 */
data class FieldValueDisplay(
    val fieldName: String,
    val value: String,
    val optionColor: String?
)
