package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class ListDetailViewModel(
    private val repository: ListBoxRepository,
    private val listId: String
) : ViewModel() {
    
    val list: StateFlow<ListEntity?> = repository.getListById(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    val items: StateFlow<List<ItemEntity>> = repository.getItemsForList(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isMultiSelectMode = MutableStateFlow(false)
    val isMultiSelectMode: StateFlow<Boolean> = _isMultiSelectMode.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    fun enterMultiSelect(itemId: String) {
        _selectedItems.value = setOf(itemId)
        _isMultiSelectMode.value = true
    }

    fun toggleItemSelection(itemId: String) {
        val updated = _selectedItems.updateAndGet { current ->
            if (current.contains(itemId)) current - itemId else current + itemId
        }
        if (updated.isEmpty()) {
            _isMultiSelectMode.value = false
        }
    }

    fun exitMultiSelect() {
        _selectedItems.value = emptySet()
        _isMultiSelectMode.value = false
    }

    fun deleteSelectedItems() {
        val toDelete = _selectedItems.value
        viewModelScope.launch {
            repository.deleteItems(toDelete)
            exitMultiSelect()
        }
    }

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
