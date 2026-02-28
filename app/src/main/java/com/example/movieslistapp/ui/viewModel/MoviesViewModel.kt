package com.example.movieslistapp.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieslistapp.BuildConfig.GEMINI_API_KEY
import com.example.movieslistapp.BuildConfig.YOUTUBE_DATAV3_API_KEY
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.repository.GetMoviesRepository
import com.example.movieslistapp.ui.UiState
import com.example.aitrailersdk.TrailerAi
import com.example.aitrailersdk.core.config.TrailerAiConfig
import com.example.aitrailersdk.core.impl.GeminiTrailerService
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
import kotlinx.coroutines.withTimeoutOrNull

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

    companion object {
        private const val TAG = "MoviesViewModel"
    }
    private val trailerAi = TrailerAi.initialize(
        TrailerAiConfig(
            enableLogging = true,
            geminiApiKey = GEMINI_API_KEY,
            youtubeApiKey = YOUTUBE_DATAV3_API_KEY
        )
    )

    private val geminiTrailerService = GeminiTrailerService(TrailerAiConfig(
        geminiApiKey = GEMINI_API_KEY,
        enableLogging = true
    ))

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

    fun getTrailerForMovie(
        imdbId: String,
        movieTitle: String,
        year: String? = null
    ): Flow<String?> = flow {
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
            val selectedGenres = listOf(
                MovieGenre.AI_SUGGESTIONS, MovieGenre.RECENTLY_ADDED, MovieGenre.ACTION, MovieGenre.COMEDY, MovieGenre.SCI_FI,
                MovieGenre.DRAMA, MovieGenre.HORROR, MovieGenre.MUSICAL, MovieGenre.THRILLER,
            )
            _carouselGenres.value = selectedGenres.map { it.displayName }

            // 1. Load "Recently Added" first (Local DB, very fast)
            launch {
                try {
                    val movies = getMoviesRepository.getRecentlyAddedMovies()
                    if (movies.isNotEmpty()) {
                        _carouselMovies.update { it + (MovieGenre.RECENTLY_ADDED.displayName to movies) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed Recently Added", e)
                } finally {
                    // Hide global loading once we have the first fast row
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

            // 2. Load other genres in parallel
            selectedGenres.filter { 
                it != MovieGenre.RECENTLY_ADDED && it != MovieGenre.AI_SUGGESTIONS 
            }.forEach { genre ->
                launch {
                    try {
                        val movies = getMoviesRepository.getMoviesByGenre(genre.name, forceRefresh)
                        if (movies.isNotEmpty()) {
                            _carouselMovies.update { it + (genre.displayName to movies) }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed genre ${genre.displayName}", e)
                    }
                }
            }

            // 3. Defer AI Suggestions (Slowest task)
            launch {
                try {
                    val aiMovies = getAiSuggestedMovies()
                    if (aiMovies.isNotEmpty()) {
                        _carouselMovies.update { it + (MovieGenre.AI_SUGGESTIONS.displayName to aiMovies) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed AI Suggestions", e)
                }
            }
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

    suspend fun getAiSuggestedMovies(): List<MovieDetails> {
        val watchedMovieListRequest = getMoviesRepository.getTopRatedMoviesOverall().take(20).map {
            TrailerRequest(movieTitle = it.Title, year = it.Year, director = it.Director, description = it.Plot, genre = it.Genre)
        }
        val movieValidator = getMoviesRepository.getAiMovieValidator()
        Log.d(TAG, "getAiSuggestedMovies: watchedMovieListRequest: $watchedMovieListRequest")
        
        return try {
            // Use withTimeoutOrNull to handle cases where the SDK call might hang
            val suggestedMovies = withTimeoutOrNull(25000) {
                geminiTrailerService.suggestRelevantMovies(watchedMovieListRequest, validator = movieValidator)
            } ?: emptyList()
            
            Log.d(TAG, "getAiSuggestedMovies: suggestedMovies: $suggestedMovies")
            
            suggestedMovies.mapNotNull { (movie, _) ->
                try {
                    val imdbId = movie.description?.split(":")?.getOrNull(1)
                    if (imdbId != null) {
                        getMoviesRepository.getMovieDetails(imdbId)
                    } else null
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get details for suggested movie: ${movie.movieTitle}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getAiSuggestedMovies", e)
            emptyList<MovieDetails>()
        }
    }

    private var searchJob: Job? = null
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

    fun incrementOpenCount(imdbId: String) {
        viewModelScope.launch {
            try {
                getMoviesRepository.incrementOpenCount(imdbId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to increment open count for $imdbId", e)
            }
        }
    }
}
