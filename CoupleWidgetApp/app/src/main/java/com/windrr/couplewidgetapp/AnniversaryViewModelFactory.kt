package com.windrr.couplewidgetapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnniversaryViewModelFactory(
    private val dao: AnniversaryDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnniversaryViewModel::class.java)) {
            return AnniversaryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}