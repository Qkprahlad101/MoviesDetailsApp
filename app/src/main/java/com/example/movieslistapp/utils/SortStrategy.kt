package com.example.movieslistapp.utils
import com.example.movieslistapp.data.model.Movie

// Filter options
enum class SortOption { NONE, TITLE, YEAR, RATING }

enum class SortOrder { ASC, DESC }

fun sortMovies(movies: List<Movie>, sortBy: SortOption, sortOrder: SortOrder): List<Movie> {
    return when (sortBy) {
        SortOption.TITLE -> {
            if (sortOrder == SortOrder.ASC) {
                movies.sortedBy { it.Title }
            } else {
                movies.sortedByDescending { it.Title }
            }
        }
        SortOption.YEAR -> {
            if (sortOrder == SortOrder.ASC) {
                movies.sortedBy { it.Year.toIntOrNull() ?: 0 }
            } else {
                movies.sortedByDescending { it.Year.toIntOrNull() ?: 0 }
            }
        }
        SortOption.RATING -> {
            if (sortOrder == SortOrder.ASC) {
                movies.sortedBy { it.Title }
            } else {
                movies.sortedByDescending { it.Title }
            }
        }
        SortOption.NONE -> movies
    }
}