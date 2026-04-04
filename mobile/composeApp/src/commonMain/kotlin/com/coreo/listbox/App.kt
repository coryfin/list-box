package com.coreo.listbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.coreo.listbox.navigation.ListBoxNavHost
import com.coreo.listbox.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        ListBoxNavHost()
    }
}