package com.example.movieslistapp.ui

import com.example.movieslistapp.data.model.Movie

data class UiState(
    val isLoading : Boolean = false,
    val movies : List<Movie> = emptyList(),
    val error : String? = null,
)
