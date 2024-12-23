package com.example.walmart.presentation.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo

class CountriesViewModelFactory(
    private val repo: CountryRepo,
    private val dispatchers: DispatcherProvider,
    private val errorFormatter: ErrorFormatter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        @Suppress("UNCHECKED_CAST")
        return CountriesViewModel(repo, dispatchers, errorFormatter) as T
    }
}