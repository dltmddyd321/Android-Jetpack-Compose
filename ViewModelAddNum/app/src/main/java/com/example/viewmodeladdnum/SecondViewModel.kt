package com.example.viewmodeladdnum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SecondViewModel(startingCnt: Int): ViewModel() {
    private var cnt = MutableLiveData<Int>()
    val cntData: LiveData<Int>
        get() = cnt

    init {
        cnt.value = startingCnt
    }

    fun updateCnt() {
        cnt.value = (cnt.value)?.plus(1)
    }
}