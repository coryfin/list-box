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
fun RenameListDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit = {}
) {
    var listTitle by remember { mutableStateOf(currentTitle) }

    val maxChars = 100
    val isValid = listTitle.trim().isNotEmpty() && listTitle.length <= maxChars

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Rename List")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = listTitle,
                    onValueChange = { newValue ->
                        if (newValue.length <= maxChars) {
                            listTitle = newValue
                        }
                    },
                    placeholder = { Text("List title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
                Text(
                    text = "${listTitle.length}/$maxChars",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onRename(listTitle.trim())
                        onDismiss()
                    }
                },
                enabled = isValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
