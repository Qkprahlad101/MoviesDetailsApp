package com.example.movieslistapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.repository.GetMoviesRepository
import com.example.movieslistapp.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val getMoviesRepository: GetMoviesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isEndReached = false
    private var isSearchInProgress = false

    var movieQuery : String = ""

    fun getMovieDetails(imdbId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val movieDetails = getMoviesRepository.getMovieDetails(imdbId)
                movieDetails.let {
                    _uiState.update { it.copy(isLoading = false, movieDetails = movieDetails) }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            } finally {
                isSearchInProgress = false
            }
        }
    }

    private var searchJob : Job? = null
    fun getSearchMovieResult(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(movies = emptyList(), isLoading = false) }
            return
        }

        if (query != movieQuery) {
            movieQuery = query
            currentPage = 1
            isEndReached = false
            _uiState.update { it.copy(movies = emptyList()) }
        }

        if (isEndReached || isSearchInProgress) return

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                delay(500L)
                _uiState.update { it.copy(isLoading = true) }
                isSearchInProgress = true

                val response = getMoviesRepository.getMoviesListFromSearch(query, currentPage)

                if (response.Response == "True") {
                    val newItems = response.Search ?: emptyList()
                    if (newItems.isEmpty()) {
                        isEndReached = true
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movies = it.movies + newItems,
                            error = null
                        )
                    }
                    currentPage++
                } else {
                    isEndReached = true
                    _uiState.update { it.copy(isLoading = false) }
                }

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown Error"
                    )
                }
            } finally {
                isSearchInProgress = false
            }
        }
    }
}
