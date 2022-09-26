package com.example.viewmodeladdnum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(startingTotal: Int): ViewModel() {
    //관찰 및 변경 가능한 LiveData 형식
    private var total = MutableLiveData<Int>()
    //public 형태가 아닌 LiveData 객체 형식으로 외부에서 접근할 것
    val totalData: LiveData<Int>
        get() = total

    init {
        total.value = startingTotal
    }

    fun setTotal(input: Int) {
        total.value = (total.value)?.plus(input)
    }
}