package com.example.jepackmvvm

import java.util.*

data class TestModel(
    val id : String = UUID.randomUUID().toString(),
    val title : String = ""
)
