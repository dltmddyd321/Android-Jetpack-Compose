package com.example.viewmodeladdnum

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThirdViewModel: ViewModel() {

    val userName = MutableLiveData<String>()

    init {
        userName.value = "Json"
    }
}