package com.example.composetodolist.ui.theme.home.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.composetodolist.Graph
import com.example.composetodolist.Todo
import com.example.composetodolist.TodoDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DetailViewModel(
    private val todoDataSource: TodoDataSource = Graph.todoRepo,
    private val id: Long
) : ViewModel() {
    private val todoText = MutableStateFlow("")
    private val todoTime = MutableStateFlow("")
    private val selectId = MutableStateFlow(-1L)

    private val _state = MutableStateFlow(DetailViewState())
    val state: StateFlow<DetailViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(todoText, todoTime, selectId) { text, time, id ->
                DetailViewState(text, time, id)
            }.collect {
                _state.value = it
            }
        }
    }

    init {
        viewModelScope.launch {
            todoDataSource.selectAll.collect { todoList ->
                todoList.find {
                    it.id == selectId.value
                }.also {
                    selectId.value = it?.id ?: -1
                    if (selectId.value != -1L) {
                        todoText.value = it?.todo ?: ""
                        todoTime.value = it?.time ?: ""
                    }
                }
            }
        }
    }

    fun onTextChange(newText: String) {
        todoText.value = newText
    }

    fun onTimeChange(newText: String) {
        todoTime.value = newText
    }

    fun insert(todo: Todo) = viewModelScope.launch {
        todoDataSource.insertTodo(todo)
    }
}

data class DetailViewState(
    val todo: String = "",
    val time: String = "",
    val selectId: Long = -1L
)

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(private val id: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(id = id) as T
        } else {
            throw IllegalArgumentException("Unknown view model class")
        }
    }
}