package com.coreo.listbox.di

import com.coreo.listbox.database.ListBoxDatabase
import com.coreo.listbox.database.DatabaseProvider
import com.coreo.listbox.repository.ListBoxRepository
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class DataComponent {
    
    @Provides
    fun provideDatabase(): ListBoxDatabase {
        return DatabaseProvider.getDatabase()
    }
    
    abstract fun listBoxRepository(): ListBoxRepository
}
