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
    
    suspend fun createListAndGetId(title: String): ListEntity? {
        return try {
            repository.createList(title)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun createListFromTemplateAndGetId(templateType: String): ListEntity? {
        return try {
            val (title, exampleItemTitle, descriptionLabels) = when (templateType) {
                "gift-ideas" -> Triple(
                    "Gift Ideas",
                    "Example Gift",
                    listOf("Store:", "Price:", "Recipient:")
                )
                "recipe-box" -> Triple(
                    "Recipe Box",
                    "Quick Recipe",
                    listOf("Ingredients:", "Instructions:")
                )
                "goal-tracker" -> Triple(
                    "Goal Tracker",
                    "Example Goal",
                    listOf("Success Criteria:", "Target Date:")
                )
                else -> return null
            }
            
            // Create the list
            val newList = repository.createList(title)
            
            // Create an example item with template labels
            val descriptionText = descriptionLabels.joinToString("\n")
            repository.createItem(newList.id, exampleItemTitle, descriptionText, 0)
            
            newList
        } catch (e: Exception) {
            null
        }
    }
}
