package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.FieldDefinitionEntity
import com.coreo.listbox.database.FieldOptionEntity
import com.coreo.listbox.database.ItemEntity
import com.coreo.listbox.model.DropdownOption
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailViewModel(
    private val repository: ListBoxRepository,
    private val itemId: String
) : ViewModel() {

    val item: StateFlow<ItemEntity?> = repository.getItemById(itemId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    /** Field definitions for the list this item belongs to */
    val fieldDefinitions: StateFlow<List<FieldDefinitionEntity>> = item
        .filterNotNull()
        .flatMapLatest { repository.getFieldDefinitionsForList(it.listId) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Map of fieldDefinitionId → list of dropdown options.
     * Only populated for DROPDOWN type fields.
     */
    val fieldOptions: StateFlow<Map<String, List<FieldOptionEntity>>> = fieldDefinitions
        .flatMapLatest { defs ->
            val dropdownDefs = defs.filter { it.dataType == "DROPDOWN" }
            if (dropdownDefs.isEmpty()) {
                kotlinx.coroutines.flow.flowOf(emptyMap())
            } else {
                val optionFlows: List<kotlinx.coroutines.flow.Flow<Pair<String, List<FieldOptionEntity>>>> =
                    dropdownDefs.map { def ->
                        repository.getFieldOptionsForDefinition(def.id)
                            .map { options -> def.id to options }
                    }
                combine(optionFlows) { pairsArray ->
                    pairsArray.toList().filterIsInstance<Pair<String, List<FieldOptionEntity>>>().toMap()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    /** Persisted field values for this item: fieldDefinitionId → value */
    val fieldValues: StateFlow<Map<String, String>> = repository.getFieldValuesForItem(itemId)
        .map { values -> values.associate { it.fieldDefinitionId to it.value_ } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    /** Draft field values while in edit mode: fieldDefinitionId → value */
    private val _draftFieldValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val draftFieldValues: StateFlow<Map<String, String>> = _draftFieldValues.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    fun enterEditMode() {
        _draftFieldValues.value = fieldValues.value
        _isEditMode.value = true
    }

    fun exitEditMode() {
        _draftFieldValues.value = emptyMap()
        _isEditMode.value = false
    }

    fun updateDraftFieldValue(fieldDefinitionId: String, value: String) {
        _draftFieldValues.value = _draftFieldValues.value + (fieldDefinitionId to value)
    }

    fun fieldValuesHaveChanged(): Boolean {
        val persisted = fieldValues.value
        val draft = _draftFieldValues.value
        val allFieldIds = fieldDefinitions.value.map { it.id }
        return allFieldIds.any { id ->
            (draft[id] ?: "") != (persisted[id] ?: "")
        }
    }

    fun saveItem(title: String, description: String?) {
        viewModelScope.launch {
            repository.updateItem(itemId, title, description)
            _draftFieldValues.value.forEach { (fieldDefId, value) ->
                if (value.isNotBlank()) {
                    repository.upsertFieldValue(itemId, fieldDefId, value)
                }
            }
            _draftFieldValues.value = emptyMap()
            _isEditMode.value = false
        }
    }

    fun addFieldDefinition(name: String, dataType: String, options: List<DropdownOption> = emptyList()) {
        val listId = item.value?.listId ?: return
        viewModelScope.launch {
            repository.createFieldDefinition(listId, name, dataType, options)
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }
}
