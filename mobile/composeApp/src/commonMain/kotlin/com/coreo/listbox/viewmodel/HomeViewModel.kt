package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ListBoxRepository) : ViewModel() {
    
    val lists: StateFlow<List<ListEntity>> = repository.getAllLists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    fun createList(title: String) {
        viewModelScope.launch {
            repository.createList(title)
        }
    }
}
