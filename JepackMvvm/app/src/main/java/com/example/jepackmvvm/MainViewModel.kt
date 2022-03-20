package com.example.jepackmvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class MainViewModel(private val repository: MyRepository) : ViewModel() {
    private val _myModels: MutableLiveData<List<TestModel>> = MutableLiveData()
    val myModels: LiveData<List<TestModel>> = _myModels

    val itemOnClickEvent: MutableLiveData<TestModel> = MutableLiveData()

    fun loadMyModels() {
        repository.getModels().let {
            _myModels.postValue(it)
        }
    }

    fun onItemClick(position: Int) {
        _myModels.value?.getOrNull(position)?.let {
            itemOnClickEvent.postValue(it)
        }
    }
}

class MainViewModelFactory(val repository: MyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class :: ${modelClass::class.java.simpleName}\")\n")
    }

}

interface MyRepository {
    fun getModels(): List<TestModel>
}

class FakeMyRepositoryImpl : MyRepository {
    override fun getModels(): List<TestModel> = listOf(
        TestModel(title = "Fake title 1"),
        TestModel(title = "Fake title 2"),
        TestModel(title = "Fake title 3"),
        TestModel(title = "Fake title 4"),
        TestModel(title = "Fake title 5"),
        TestModel(title = "Fake title 6"),
        TestModel(title = "Fake title 7"),
        TestModel(title = "Fake title 8"),
        TestModel(title = "Fake title 9"),
        TestModel(title = "Fake title 10")
    )
}