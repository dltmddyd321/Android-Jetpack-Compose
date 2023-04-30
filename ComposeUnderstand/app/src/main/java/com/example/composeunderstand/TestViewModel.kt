package com.example.composeunderstand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestViewModel: ViewModel() {
    private val _text: MutableLiveData<String> = MutableLiveData()
    val text: LiveData<String> get() = _text

    fun updateText(t: String) {
        _text.postValue(t)
    }
}