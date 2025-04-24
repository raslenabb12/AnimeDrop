package com.youme.animedrop.pagingmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.youme.animedrop.data.model.AnimeCountdown
import com.youme.animedrop.pagingmodel.AnimePagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest


class AnimeViewModel : ViewModel() {
    private val _sortpopular = MutableStateFlow<Boolean>(false)
    val sortpopular: StateFlow<Boolean> = _sortpopular.asStateFlow()
    val AnimeFlow: Flow<PagingData<AnimeCountdown>> = sortpopular
        .flatMapLatest { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 25,
                    prefetchDistance = 5,
                    enablePlaceholders = false,
                    initialLoadSize = 25
                ),
                pagingSourceFactory = {
                    AnimePagingSource(stat_sort = query)
                }
            ).flow
        }
        .cachedIn(viewModelScope)
    fun setSortpopular(stat:Boolean){
        _sortpopular.value=stat
    }
}