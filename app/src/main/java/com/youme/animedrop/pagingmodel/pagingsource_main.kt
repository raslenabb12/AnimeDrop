package com.youme.animedrop.pagingmodel

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.youme.animedrop.data.model.AnimeCountdown
import com.youme.animedrop.data.network.AnimeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.time.OffsetDateTime

class AnimePagingSource(val stat_sort:Boolean) : PagingSource<Int, AnimeCountdown>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeCountdown> {
        val page = params.key ?: 1
        val perPage = params.loadSize
        return try {
            val responseJson = withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.jikan.moe/v4/seasons/upcoming?page=${page}")
                client.newCall(request.get().build()).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        throw Exception("HTTP error ${response.code}: $errorBody")
                    }
                    response.body?.string() ?: throw Exception("Empty response body")
                }
            }
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter = moshi.adapter(AnimeResponse::class.java)
            val apiResponse = jsonAdapter.fromJson(responseJson)
            val list = mutableListOf<AnimeCountdown>()
            if (!apiResponse?.data.isNullOrEmpty()) {
                apiResponse?.data?.filter { it.aired.from!=null &&
                        OffsetDateTime.parse(it.aired.from).toLocalDateTime().isAfter(LocalDateTime.now())
                }?.forEach {
                    list.add(AnimeCountdown(
                        title = it.title,
                        imageUrl = it.images.jpg.image_url,
                        releaseDateTime = OffsetDateTime.parse(it.aired.from).toLocalDateTime()

                    ))
                }
                LoadResult.Page(
                    data = list,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (apiResponse?.data?.size!! < perPage) null else page + 1
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (apiResponse?.data?.size!! < perPage) null else page + 1
                )
            }
        } catch (e: Exception) {
            Log.e("AnimePagingSource", "Error in load function", e)
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, AnimeCountdown>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
