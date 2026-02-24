package com.example.movieslistapp.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieslistapp.BuildConfig.GEMINI_API_KEY
import com.example.movieslistapp.BuildConfig.YOUTUBE_DATAV3_API_KEY
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.repository.GetMoviesRepository
import com.example.movieslistapp.ui.UiState
import com.example.aitrailersdk.TrailerAi
import com.example.aitrailersdk.core.config.TrailerAiConfig
import com.example.aitrailersdk.core.model.TrailerRequest
import com.example.movieslistapp.utils.MovieGenre
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
    private var lastSearchedQuery: String = ""

    private val _movieQuery = MutableStateFlow("")
    val movieQuery: StateFlow<String> = _movieQuery.asStateFlow()

    private val _carouselGenres = MutableStateFlow<List<String>>(emptyList())
    val carouselGenres: StateFlow<List<String>> = _carouselGenres.asStateFlow()

    private val _carouselMovies = MutableStateFlow<Map<String, List<MovieDetails>>>(emptyMap())
    val carouselMovies: StateFlow<Map<String, List<MovieDetails>>> = _carouselMovies.asStateFlow()
    private var isFirstLoad = true
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val trailerAi = TrailerAi.initialize(
        TrailerAiConfig(
            enableLogging = true,
            geminiApiKey = GEMINI_API_KEY,
            youtubeApiKey = YOUTUBE_DATAV3_API_KEY
        )
    )

    fun updateSearchQuery(query: String) {
        _movieQuery.value = query
        getSearchMovieResult(query)
    }

    fun refreshCarousel() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadCarouselData(forceRefresh = true)
            _isRefreshing.value = false
        }
    }
    fun getTrailerForMovie(imdbId: String, movieTitle: String, year: String? = null): Flow<String?> = flow {
        if (movieTitle.isBlank()) {
            emit(null)
            return@flow
        }
        // 1. Check DB first
        val cachedTrailer = getMoviesRepository.getTrailerUrlFromDb(imdbId)
        if (cachedTrailer != null) {
            emit(cachedTrailer)
            return@flow
        }

        // 2. If not in DB, call SDK
        val request = TrailerRequest(
            movieTitle = movieTitle,
            year = year
        )

        val result = trailerAi.findTrailer(request)
        val trailerUrl = when (result) {
            is com.example.aitrailersdk.core.model.TrailerResult.Success -> result.url
            else -> null
        }

        // 3. Update DB if trailer found
        if (trailerUrl != null) {
            getMoviesRepository.updateTrailerUrl(imdbId, trailerUrl)
        }

        emit(trailerUrl)
    }

    fun loadCarouselData(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val selectedGenres = listOf(MovieGenre.RECENTLY_VIEWED, MovieGenre.ACTION, MovieGenre.COMEDY, MovieGenre.SCI_FI,
                MovieGenre.DRAMA, MovieGenre.HORROR, MovieGenre.MUSICAL, MovieGenre.THRILLER,
                )
            _carouselGenres.value = selectedGenres.map { it.displayName }

            val moviesByGenre = mutableMapOf<String, List<MovieDetails>>()
            moviesByGenre[MovieGenre.RECENTLY_VIEWED.displayName] = getMoviesRepository.getRecentlySearchedMovies() ?: emptyList()
            selectedGenres.filter { it.name != MovieGenre.RECENTLY_VIEWED.name }.forEach { genre ->
                moviesByGenre[genre.displayName] = getMoviesRepository.getMoviesByGenre(genre.name, forceRefresh)
            }
            _carouselMovies.value = moviesByGenre.filter { it.value.isNotEmpty() }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    fun getMovieDetails(imdbId: String) {
        _uiState.update { it.copy(isLoading = true, movieDetails = null) } // Clear previous details
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
            lastSearchedQuery = ""
            currentPage = 1
            isEndReached = false
            return
        }

        // If the query is different from the last actual search, reset pagination.
        if (query != lastSearchedQuery) {
            lastSearchedQuery = query
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
