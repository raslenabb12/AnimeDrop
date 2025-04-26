package com.youme.animedrop.data.network
import retrofit2.http.GET
import retrofit2.Call

data class AnimeResponse(
    val data: List<AnimeData>
)

data class AnimeData(
    val title: String,
    val images: ImageWrapper,
    val aired: Aired,
    val broadcast:Broadcast?=null
)
data class Broadcast(
    val time :String?=null
)
data class ImageWrapper(
    val jpg: ImageUrl
)

data class ImageUrl(
    val image_url: String
)

data class Aired(
    val from: String?
)