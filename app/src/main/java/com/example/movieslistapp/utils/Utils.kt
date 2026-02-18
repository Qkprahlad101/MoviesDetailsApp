package com.example.movieslistarchless.utils

object Utils {
    fun getFilterDisplayName(sortBy: SortOption, sortOrder: SortOrder): String {
        return when (sortBy) {
            SortOption.NONE -> "None"
            SortOption.TITLE -> if (sortOrder == SortOrder.ASC) "Title (A-Z)" else "Title (Z-A)"
            SortOption.YEAR -> if (sortOrder == SortOrder.ASC) "Year (Old)" else "Year (New)"
            SortOption.RATING -> if (sortOrder == SortOrder.ASC) "Rating (Low)" else "Rating (High)"
        }
    }
}