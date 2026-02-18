package com.example.movieslistapp.ui

import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MovieDetails

data class UiState(
    val isLoading : Boolean = false,
    val movies : List<Movie> = emptyList(),
    val movieDetails: MovieDetails? = null,
    val error : String? = null,
)
