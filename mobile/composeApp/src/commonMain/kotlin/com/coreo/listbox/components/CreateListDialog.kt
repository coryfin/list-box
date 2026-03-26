package com.coreo.listbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun CreateListDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit = {}
) {
    var listTitle by remember { mutableStateOf("") }

    val maxChars = 100
    val isTitleTooLong = listTitle.length > maxChars
    val isValid = listTitle.trim().isNotEmpty() && !isTitleTooLong

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New List")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = listTitle,
                    onValueChange = { listTitle = it },
                    placeholder = { Text("List title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isTitleTooLong,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isTitleTooLong) {
                                Text(
                                    text = "Max $maxChars characters",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                            Text(
                                text = "${listTitle.length}/$maxChars",
                                color = if (isTitleTooLong) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onCreate(listTitle.trim())
                        onDismiss()
                    }
                },
                enabled = isValid
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
