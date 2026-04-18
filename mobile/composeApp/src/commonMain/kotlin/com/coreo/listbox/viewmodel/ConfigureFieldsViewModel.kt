package com.coreo.listbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreo.listbox.database.FieldDefinitionEntity
import com.coreo.listbox.database.FieldOptionEntity
import com.coreo.listbox.database.ListEntity
import com.coreo.listbox.model.DropdownOption
import com.coreo.listbox.repository.ListBoxRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigureFieldsViewModel(
    private val repository: ListBoxRepository,
    val listId: String
) : ViewModel() {

    val fieldDefinitions: StateFlow<List<FieldDefinitionEntity>> =
        repository.getFieldDefinitionsForList(listId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val list: StateFlow<ListEntity?> = repository.getListById(listId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val fieldOptions: StateFlow<Map<String, List<FieldOptionEntity>>> = fieldDefinitions
        .flatMapLatest { defs ->
            val dropdownDefs = defs.filter { it.dataType == "DROPDOWN" }
            if (dropdownDefs.isEmpty()) {
                flowOf(emptyMap())
            } else {
                val flows = dropdownDefs.map { def ->
                    repository.getFieldOptionsForDefinition(def.id)
                        .map { opts -> def.id to opts }
                }
                combine(flows) { pairs ->
                    pairs.toList().filterIsInstance<Pair<String, List<FieldOptionEntity>>>().toMap()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun addField(name: String, dataType: String, options: List<DropdownOption>) {
        viewModelScope.launch {
            repository.createFieldDefinition(listId, name, dataType, options)
        }
    }

    fun updateField(fieldId: String, name: String, dataType: String, options: List<DropdownOption>) {
        viewModelScope.launch {
            repository.updateFieldDefinition(fieldId, name, dataType, options)
        }
    }

    fun deleteField(fieldId: String) {
        viewModelScope.launch {
            repository.deleteFieldDefinition(fieldId)
        }
    }

    fun toggleVisibility(fieldId: String, currentVisible: Boolean) {
        viewModelScope.launch {
            repository.updateFieldDefinitionVisibility(fieldId, !currentVisible)
        }
    }
}
