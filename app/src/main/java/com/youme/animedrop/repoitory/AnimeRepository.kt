package com.youme.animedrop.repoitory
import android.util.Log
import com.youme.animedrop.data.model.AnimeCountdown
import com.youme.animedrop.data.network.JikanService
import java.time.OffsetDateTime

class AnimeRepository(
    private val jikanService: JikanService
) {
    suspend fun fetchSeasonalAnime(): List<AnimeCountdown> {
        val response = jikanService.getSeasonalAnime().execute()
        if (!response.isSuccessful || response.body() == null) return emptyList()

        return response.body()!!.data.mapNotNull { anime ->
            try {
                val time = anime.aired.from ?: return@mapNotNull null
                val dateTime = OffsetDateTime.parse(time).toLocalDateTime()

                AnimeCountdown(
                    title = anime.title,
                    releaseDateTime = dateTime,
                    imageUrl = anime.images.jpg.image_url
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
