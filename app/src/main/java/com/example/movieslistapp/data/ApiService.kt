package com.example.movieslistapp.data

import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MoviesListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/")
    suspend fun getMoviesListFromSearch(
        @Query("s") query: String,
        @Query("page") page: Int = 1
    ): MoviesListResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String
    ): Movie
}