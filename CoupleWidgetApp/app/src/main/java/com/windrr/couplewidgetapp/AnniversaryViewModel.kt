package com.windrr.couplewidgetapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnniversaryViewModel(
    private val dao: AnniversaryDao
) : ViewModel() {

    private val _state = MutableStateFlow(AnniversaryState())
    val state: StateFlow<AnniversaryState> = _state.asStateFlow()

    private val _effect = Channel<AnniversarySideEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(AnniversaryIntent.LoadAnniversaries)
    }

    fun handleIntent(intent: AnniversaryIntent) {
        when (intent) {
            is AnniversaryIntent.LoadAnniversaries -> loadAnniversaries()
            is AnniversaryIntent.AddAnniversary -> addAnniversary(intent)
            is AnniversaryIntent.DeleteAnniversary -> deleteAnniversary(intent.id)
        }
    }

    private fun loadAnniversaries() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val items = dao.getAll()
                _state.update { it.copy(anniversaries = items, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(AnniversarySideEffect.ShowToast("로드 실패: ${e.message}"))
            }
        }
    }

    private fun addAnniversary(intent: AnniversaryIntent.AddAnniversary) {
        viewModelScope.launch {
            val newItem = AnniversaryItem(
                title = intent.title,
                dateMillis = intent.dateMillis,
                dateCount = intent.dateCount
            )
            dao.insert(newItem)

            loadAnniversaries()
            _effect.send(AnniversarySideEffect.ShowToast("기념일이 등록되었습니다!"))
        }
    }

    private fun deleteAnniversary(id: Int) {
        viewModelScope.launch {
            dao.deleteById(id)
            loadAnniversaries()
            _effect.send(AnniversarySideEffect.ShowToast("삭제되었습니다."))
        }
    }
}