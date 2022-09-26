package com.example.viewmodeladdnum

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private var total = 0

    fun getTotal(): Int {
        return total
    }

    fun setTotal(input: Int) {
        total += input
    }
}