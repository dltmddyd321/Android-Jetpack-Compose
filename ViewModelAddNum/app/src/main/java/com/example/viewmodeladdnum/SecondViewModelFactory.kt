package com.example.viewmodeladdnum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class SecondViewModelFactory(private val startingCnt: Int): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecondViewModel::class.java)) {
            return SecondViewModel(startingCnt) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}
