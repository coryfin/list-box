package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListDetailViewModel(
    private val repository: ListBoxRepository,
    private val listId: String
) : ViewModel() {
    
    val list: StateFlow<ListEntity?> = repository.getListById(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    val items: StateFlow<List<ItemEntity>> = repository.getItemsForList(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun createItem(title: String, description: String? = null) {
        viewModelScope.launch {
            repository.createItem(listId, title, description)
        }
    }
    
    fun deleteList() {
        viewModelScope.launch {
            repository.deleteList(listId)
        }
    }
    
    fun updateListTitle(newTitle: String) {
        viewModelScope.launch {
            repository.updateListTitle(listId, newTitle)
        }
    }
}
