
package com.youme.animedrop.viewmodel
import androidx.lifecycle.*
import com.youme.animedrop.data.model.AnimeCountdown
import com.youme.animedrop.repoitory.AnimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CountdownViewModel(
    private val repository: AnimeRepository
) : ViewModel() {
    private val _seasonalAnime = MutableLiveData<List<AnimeCountdown>>()
    val seasonalAnime: LiveData<List<AnimeCountdown>> = _seasonalAnime
    fun loadSeasonalAnime() {
        viewModelScope.launch (Dispatchers.IO){
            try {
                val animeList = repository.fetchSeasonalAnime()
                _seasonalAnime.postValue(animeList)
            } catch (e: Exception) {
                e.printStackTrace()
                _seasonalAnime.postValue(emptyList())
            }
        }
    }
}
class CountdownViewModelFactory(
    private val repository: AnimeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountdownViewModel::class.java)) {
            return CountdownViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}