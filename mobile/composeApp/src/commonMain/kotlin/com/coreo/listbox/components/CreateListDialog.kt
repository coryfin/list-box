package com.coreo.listbox.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun CreateListDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit = {}
) {
    var listTitle by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    
    val maxChars = 100
    val isValid = listTitle.trim().isNotEmpty() && listTitle.length <= maxChars

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New List")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = listTitle,
                    onValueChange = { newValue ->
                        if (newValue.length <= maxChars) {
                            listTitle = newValue
                            isError = false
                        }
                    },
                    label = { Text("List Title") },
                    placeholder = { Text("Enter list name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
                Text(
                    text = "${listTitle.length}/$maxChars",
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (isError) {
                    Text(
                        text = "List title is required",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onCreate(listTitle.trim())
                        onDismiss()
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
