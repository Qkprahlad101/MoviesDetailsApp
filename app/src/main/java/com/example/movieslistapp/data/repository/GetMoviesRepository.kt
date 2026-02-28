package com.example.movieslistapp.data.repository

import android.util.Log
import androidx.collection.LruCache
import com.example.movieslistapp.data.ApiService
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.model.MovieResponse
import com.example.movieslistapp.db.dao.MovieDao
import com.example.movieslistapp.domain.mapper.toMovie
import com.example.movieslistapp.domain.mapper.toMovieDetails
import com.example.movieslistapp.domain.mapper.toMovieDetailsEntity
import com.example.movieslistapp.domain.mapper.toMovieEntity
import com.example.movieslistapp.utils.MovieGenre

class GetMoviesRepository(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {

    companion object {
        private const val MAX_DB_MOVIES = 200
        private const val MAX_DB_MOVIE_DETAILS = 200
        private const val PAGE_SIZE = 10
        private const val TAG = "GetMoviesRepository"
    }

    private val movieDetailsCache = LruCache<String, MovieDetails>(50)
    private val moviesListCache = LruCache<String, MovieResponse>(20)

    private suspend fun enforceDatabaseLimits() {
        val movieCount = movieDao.getMovieCount()
        val detailsCount = movieDao.getMovieDetailsCount()

        if (movieCount > MAX_DB_MOVIES) {
            movieDao.deleteOldestMovies(movieCount - MAX_DB_MOVIES)
        }

        if (detailsCount > MAX_DB_MOVIE_DETAILS) {
            movieDao.deleteOldestMovieDetails(detailsCount - MAX_DB_MOVIE_DETAILS)
        }
    }

    suspend fun getMoviesListFromSearch(query: String, currentPage: Int, year: String? = null): MovieResponse {
        val cacheKey = "$query:$currentPage"
        //check in cache
        moviesListCache[cacheKey]?.let {
            return it
        }

        //check in db
        val offset = (currentPage - 1) * PAGE_SIZE
        val dbMoviesList = movieDao.getMoviesByQueryPaginated(query, PAGE_SIZE, offset)
        if (dbMoviesList.isNotEmpty()) {
            val response = MovieResponse(
                Search = dbMoviesList.filter { it.Poster != "N/A" && it.Poster.isNotEmpty()
                        && it.Title != "N/A" && it.Title.isNotEmpty() && it.Year != "N/A" && it.Year.isNotEmpty() }
                    .map { it.toMovie() },
                Error = "",
                Response = "True"
            )
            moviesListCache.put(cacheKey, response)
            return response
        } else {
            //api call
            val response = apiService.getMoviesListFromSearch(query, currentPage, year)
            response.Search?.let { movies ->
                movieDao.insertMovies(movies.filter { it.Poster != "N/A" && it.Poster.isNotEmpty()
                        && it.Title != "N/A" && it.Title.isNotEmpty() && it.Year != "N/A" && it.Year.isNotEmpty() }
                    .map { it.toMovieEntity(query) })
                enforceDatabaseLimits()
                moviesListCache.put(cacheKey, response)
            }
            return response
        }
    }

    fun getAiMovieValidator(): AiMovieValidator {
        return AiMovieValidator(apiService)
    }

    suspend fun incrementOpenCount(imdbId: String) {
        movieDao.incrementOpenCount(imdbId)
    }

    suspend fun getMovieDetails(imdbId: String): MovieDetails {

        //first check in cache and return it
        movieDetailsCache[imdbId]?.let {
            return it
        }

        //check database
        val dbDetails = movieDao.getMovieDetails(imdbId)
        if (dbDetails != null) {
            val details = dbDetails.toMovieDetails()
            movieDetailsCache.put(imdbId, details)
            return details
        } else {
            //api call
            val details = apiService.getMovieDetails(imdbId)
            movieDao.insertMovieDetails(details.toMovieDetailsEntity())
            enforceDatabaseLimits()
            movieDetailsCache.put(imdbId, details)
            return details
        }
    }

    suspend fun getRecentlyAddedMovies() : List<MovieDetails> {
        triggerDataRefresh()
        return movieDao.getRecentlyAddedMovies().map { it.toMovieDetails() }
    }

    suspend fun triggerDataRefresh() {

        val latestMovies = apiService.getMoviesListFromSearch("movie", 1, "2026").Search
        latestMovies?.let {
            movieDao.insertMovies(it.map { it.toMovieEntity("movie") })
        }
        enforceDatabaseLimits()

        latestMovies?.forEach { movie ->
            getMovieDetails(movie.imdbID)
        }
    }

    suspend fun getAllGenres(): List<String> {
        return movieDao.getAllGenres()
    }

    suspend fun getTopRatedMoviesByGenre(genre: String): List<MovieDetails> {
        return movieDao.getTopRatedMoviesByGenre(genre).map { it.toMovieDetails() }
    }

    suspend fun getTopRatedMoviesOverall(): List<MovieDetails> {
        return movieDao.getTopRatedMoviesOverall().map { it.toMovieDetails() }
    }

    suspend fun getTrailerUrlFromDb(imdbId: String): String? {
        // Check movie_details table first as it's more likely to have detailed info
        return movieDao.getMovieDetailsTrailer(imdbId) ?: movieDao.getMovieTrailer(imdbId)
    }

    suspend fun getMoviesByGenre(genreName: String, forceRefresh: Boolean = false): List<MovieDetails> {
        val normalizedGenre = MovieGenre.fromString(genreName)?.displayName ?: genreName
        
        try {
            // Step 1: Check local database first if not forcing refresh
            if (!forceRefresh) {
                val cachedMovies = movieDao.getTopRatedMoviesByGenre(normalizedGenre)
                if (cachedMovies.isNotEmpty()) {
                    return cachedMovies.map { it.toMovieDetails() }
                }
            }

            // Step 2: Fetch from search API
            val searchResponse = try {
                apiService.getMoviesListFromSearch(normalizedGenre)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to search movies for genre $normalizedGenre", e)
                null
            }

            if (searchResponse != null) {
                try {
                    searchResponse.Search?.take(10).let { movies ->
                        val movieEntities = movies?.map { it.toMovieEntity(normalizedGenre) }
                        movieDao.insertMovies(movieEntities!!)
                        movieEntities.forEach {
                            getMovieDetails(it.imdbId)
                        }
                        enforceDatabaseLimits()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get movies by genre $normalizedGenre", e)
                }
            }
            return movieDao.getTopRatedMoviesByGenre(normalizedGenre).map { it.toMovieDetails() }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in getMoviesByGenre for $normalizedGenre", e)
            return movieDao.getTopRatedMoviesByGenre(normalizedGenre).map { it.toMovieDetails() }
        }
    }

    suspend fun updateTrailerUrl(imdbId: String, trailerUrl: String) {
        movieDao.updateMovieDetailsTrailer(imdbId, trailerUrl)
        movieDao.updateMovieTrailer(imdbId, trailerUrl)
    }
}
