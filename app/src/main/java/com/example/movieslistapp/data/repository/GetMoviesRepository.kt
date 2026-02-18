package com.example.movieslistapp.data.repository

import com.example.movieslistapp.data.ApiService
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.model.MovieResponse

class GetMoviesRepository(
    private val apiService : ApiService
) {

    suspend fun getMoviesListFromSearch(query : String, currentPage: Int) : MovieResponse {
        return apiService.getMoviesListFromSearch(query, currentPage)
    }

    suspend fun getMovieDetails(imdbId: String) : MovieDetails {
        return apiService.getMovieDetails(imdbId)
    }
}