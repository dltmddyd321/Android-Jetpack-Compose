package com.windrr.couplewidgetapp

sealed interface AnniversarySideEffect {
    data class ShowToast(val message: String) : AnniversarySideEffect
}