package com.pivopiev.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    object LoadingMore : UiState()
    data class Success(val videos: List<VideoItem>, val hasMore: Boolean) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel : ViewModel() {

    private val repository = VideoRepository()

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val allVideos = mutableListOf<VideoItem>()
    private var nextPageToken: String? = null
    private var isLoadingMore = false

    fun loadVideos(apiKey: String, channelHandle: String, refresh: Boolean = false) {
        if (refresh) {
            allVideos.clear()
            nextPageToken = null
            repository.clearCache()
            _uiState.value = UiState.Loading
        } else {
            _uiState.value = UiState.Loading
        }

        viewModelScope.launch {
            val result = repository.fetchVideos(apiKey, channelHandle)
            result.fold(
                onSuccess = { (videos, token) ->
                    allVideos.addAll(videos)
                    nextPageToken = token
                    _uiState.value = UiState.Success(
                        videos = allVideos.toList(),
                        hasMore = token != null
                    )
                },
                onFailure = { e ->
                    _uiState.value = UiState.Error(e.message ?: "Неизвестна грешка")
                }
            )
        }
    }

    fun loadMore(apiKey: String, channelHandle: String) {
        if (isLoadingMore || nextPageToken == null) return
        isLoadingMore = true
        _uiState.value = UiState.LoadingMore

        viewModelScope.launch {
            val result = repository.fetchVideos(apiKey, channelHandle, nextPageToken)
            result.fold(
                onSuccess = { (videos, token) ->
                    allVideos.addAll(videos)
                    nextPageToken = token
                    _uiState.value = UiState.Success(
                        videos = allVideos.toList(),
                        hasMore = token != null
                    )
                },
                onFailure = { e ->
                    _uiState.value = UiState.Error(e.message ?: "Неизвестна грешка")
                }
            )
            isLoadingMore = false
        }
    }
}
