package com.example.ktorcallapi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {

    val usersFlow = MutableStateFlow<List<User>>(listOf())

    init {
        Log.d("TAG", "UserVM() init called")

        viewModelScope.launch {
            kotlin.runCatching {
                UserRepo.fetchUsers()
            }.onSuccess {
                usersFlow.value = it
                Log.d("TAG", "UserVM() Success")
            }.onFailure {
                Log.d("TAG", "UserVM() Failed : ${it.localizedMessage}")
            }
        }
    }
}