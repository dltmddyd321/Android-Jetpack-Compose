package com.example.composetodolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    val todo: String,
    val time: String,
    val isComplete: Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Long
)
