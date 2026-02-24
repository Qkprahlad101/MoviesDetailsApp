package com.example.movieslistapp.data

import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/")
    suspend fun getMoviesListFromSearch(
        @Query("s") query: String,
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String
    ): MovieDetails

    @GET("/")
    suspend fun searchMoviesForSpecifiGenre(
        @Query("s") searchTerm: String,          // e.g. "Action", "sci-fi horror", "Marvel superhero"
        @Query("type") type: String = "movie",
        @Query("page") page: Int = 1
    ): MovieResponse
}