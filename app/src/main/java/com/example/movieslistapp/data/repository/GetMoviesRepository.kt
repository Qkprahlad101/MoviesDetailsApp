package com.example.movieslistapp.data.repository

import com.example.movieslistapp.data.ApiService
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MoviesListResponse

class GetMoviesRepository(
    private val apiService : ApiService
) {

    suspend fun getMoviesListFromSearch(query : String, currentPage: Int) : MoviesListResponse {
        return apiService.getMoviesListFromSearch(query, currentPage)
    }

    suspend fun getMovieDetails(imdbId: String) : Movie {
        return apiService.getMovieDetails(imdbId)
    }
}