package com.windrr.couplewidgetapp

data class AnniversaryState(
    val anniversaries: List<AnniversaryItem> = emptyList(),
    val isLoading: Boolean = false
)