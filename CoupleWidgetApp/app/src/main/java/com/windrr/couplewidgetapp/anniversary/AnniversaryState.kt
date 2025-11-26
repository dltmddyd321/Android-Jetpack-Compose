package com.windrr.couplewidgetapp.anniversary

data class AnniversaryState(
    val anniversaries: List<AnniversaryItem> = emptyList(),
    val isLoading: Boolean = false
)