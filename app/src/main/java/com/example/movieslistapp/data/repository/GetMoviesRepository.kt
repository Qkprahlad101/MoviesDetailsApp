package com.example.movieslistapp.data.repository

import androidx.collection.LruCache
import com.example.movieslistapp.data.ApiService
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.model.MovieResponse
import com.example.movieslistapp.db.dao.MovieDao
import com.example.movieslistapp.domain.mapper.toMovie
import com.example.movieslistapp.domain.mapper.toMovieDetails
import com.example.movieslistapp.domain.mapper.toMovieDetailsEntity
import com.example.movieslistapp.domain.mapper.toMovieEntity

class GetMoviesRepository(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {

    companion object {
        private const val MAX_DB_MOVIES = 200
        private const val MAX_DB_MOVIE_DETAILS = 200
        private const val PAGE_SIZE = 10
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

    suspend fun getMoviesListFromSearch(query: String, currentPage: Int): MovieResponse {
        val cacheKey = "$query:$currentPage"
        //check in cache
        moviesListCache.get(cacheKey)?.let {
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
            val response = apiService.getMoviesListFromSearch(query, currentPage)
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

    suspend fun getMovieDetails(imdbId: String): MovieDetails {
        //first check in cache and return it
        movieDetailsCache.get(imdbId)?.let {
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

    suspend fun updateTrailerUrl(imdbId: String, trailerUrl: String) {
        movieDao.updateMovieDetailsTrailer(imdbId, trailerUrl)
        movieDao.updateMovieTrailer(imdbId, trailerUrl)
    }
}
