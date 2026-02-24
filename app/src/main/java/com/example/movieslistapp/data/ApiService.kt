package com.example.movieslistapp.data

import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/")
    suspend fun getMoviesListFromSearch(
        @Query("s") query: String,  // e.g. "Action", "sci-fi horror", "Marvel superhero"
        @Query("page") page: Int = 1,
        @Query("y") year: String? = null,
        @Query("type") type: String = "movie",
    ): MovieResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String
    ): MovieDetails

}