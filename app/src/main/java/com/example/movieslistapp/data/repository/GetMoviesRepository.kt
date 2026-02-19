package com.example.movieslistapp.data.repository

import com.example.movieslistapp.data.ApiService
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
    suspend fun getMoviesListFromSearch(query: String, currentPage: Int): MovieResponse {
        val cachedMovies = movieDao.getMoviesByQuery(query)

        return if (cachedMovies.isNotEmpty()) {
            MovieResponse(
                Search = cachedMovies.map { it.toMovie() },
                Error = "",
                Response = "True"
            )
        } else {
            val response = apiService.getMoviesListFromSearch(query, currentPage)
            response.Search?.let { movies ->
                movieDao.insertMovies(movies.map { it.toMovieEntity(query) })
            }
            response
        }
    }

    suspend fun getMovieDetails(imdbId: String): MovieDetails {
        val cachedDetails = movieDao.getMovieDetails(imdbId)

        return if (cachedDetails != null) {
            cachedDetails.toMovieDetails()
        } else {
            val details = apiService.getMovieDetails(imdbId)
            movieDao.insertMovieDetails(details.toMovieDetailsEntity())
            details
        }
    }
}