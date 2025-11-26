package com.windrr.couplewidgetapp.anniversary

sealed interface AnniversarySideEffect {
    data class ShowToast(val message: String) : AnniversarySideEffect
}