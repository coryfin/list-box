package com.coreo.listbox

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.coreo.listbox.navigation.ListBoxNavHost

@Composable
@Preview
fun App() {
    MaterialTheme {
        ListBoxNavHost()
    }
}