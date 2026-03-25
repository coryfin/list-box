package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val repository: ListBoxRepository,
    private val itemId: String
) : ViewModel() {

    val item: StateFlow<ItemEntity?> = repository.getItemById(itemId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun deleteItem() {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }
}
