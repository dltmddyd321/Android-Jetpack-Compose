package com.windrr.couplewidgetapp.anniversary

sealed interface AnniversaryIntent {
    data object LoadAnniversaries : AnniversaryIntent
    data class AddAnniversary(val title: String, val dateMillis: Long, val dateCount: Int) :
        AnniversaryIntent
    data class DeleteAnniversary(val id: Int) : AnniversaryIntent
}