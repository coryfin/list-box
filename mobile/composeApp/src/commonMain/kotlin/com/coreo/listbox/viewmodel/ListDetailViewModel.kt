package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.model.FieldValueDisplay
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

enum class ListInteractionState {
    Default,
    SelectOrDrag,
    MultiSelect,
    Dragging
}

class ListDetailViewModel(
    private val repository: ListBoxRepository,
    private val listId: String
) : ViewModel() {

    val list: StateFlow<ListEntity?> = repository.getListById(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val items: StateFlow<List<ItemEntity>> = repository.getItemsForList(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val itemCustomFieldValues: StateFlow<Map<String, List<FieldValueDisplay>>> =
        repository.getItemsWithVisibleFieldValues(listId)
            .map { rows ->
                rows
                    .filter { it.fieldValue != null }
                    .groupBy { it.itemId }
                    .mapValues { (_, itemRows) ->
                        itemRows.map { row ->
                            FieldValueDisplay(
                                fieldName = row.fieldName,
                                value = row.fieldValue!!,
                                optionColor = row.optionColor?.takeIf { it.isNotBlank() }
                            )
                        }
                    }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    private val _listInteractionState = MutableStateFlow(ListInteractionState.Default)
    val listInteractionState: StateFlow<ListInteractionState> = _listInteractionState.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedItemIds: StateFlow<Set<String>> = _selectedItemIds.asStateFlow()

    private val _orderedItems = MutableStateFlow<List<ItemEntity>>(emptyList())
    val orderedItems: StateFlow<List<ItemEntity>> = _orderedItems.asStateFlow()

    init {
        viewModelScope.launch {
            items.collect { newItems ->
                if (_listInteractionState.value == ListInteractionState.Default) {
                    _orderedItems.value = newItems
                }
            }
        }
    }

    // --- Drag / selection state machine ---

    fun onDragStarted(itemId: String) {
        if (_listInteractionState.value == ListInteractionState.Default) {
            _listInteractionState.value = ListInteractionState.SelectOrDrag
            _selectedItemIds.value = setOf(itemId)
        }
    }

    fun onItemMoved(fromIndex: Int, toIndex: Int) {
        if (_listInteractionState.value == ListInteractionState.SelectOrDrag) {
            _listInteractionState.value = ListInteractionState.Dragging
            _selectedItemIds.value = emptySet()
        }
        _orderedItems.value = _orderedItems.value.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    }

    fun onDragEnded() {
        when (_listInteractionState.value) {
            ListInteractionState.SelectOrDrag -> _listInteractionState.value = ListInteractionState.MultiSelect
            ListInteractionState.Dragging -> {
                _listInteractionState.value = ListInteractionState.Default
                saveOrderedItems()
            }
            else -> {}
        }
    }

    fun toggleItemSelection(itemId: String) {
        val updated = _selectedItemIds.updateAndGet { current ->
            if (current.contains(itemId)) current - itemId else current + itemId
        }
        if (updated.isEmpty()) {
            _listInteractionState.value = ListInteractionState.Default
        }
    }

    fun exitMultiSelect() {
        _selectedItemIds.value = emptySet()
        _listInteractionState.value = ListInteractionState.Default
    }

    // --- Data operations ---

    private fun saveOrderedItems() {
        viewModelScope.launch {
            val orderUpdates = _orderedItems.value.mapIndexed { index, item ->
                item.id to index.toLong()
            }
            repository.reorderItems(orderUpdates)
        }
    }

    fun deleteSelectedItems() {
        val toDelete = _selectedItemIds.value
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
