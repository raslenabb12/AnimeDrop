package com.youme.animedrop.data.model

import java.time.LocalDateTime

data class AnimeCountdown(
    val id: Int = 0,
    val title: String,
    val releaseDateTime: LocalDateTime,
    val imageUrl: String?,
    val broadcast_time:String?=null
)