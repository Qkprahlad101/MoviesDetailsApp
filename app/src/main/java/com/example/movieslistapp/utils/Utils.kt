package com.example.movieslistapp.utils

object Utils {
    // AI Suggestion Cache Configuration
    const val AI_SUGGESTION_CACHE_DURATION_MS = 24L * 60L * 60L * 1000L // 24 hours in milliseconds
    
    fun getFilterDisplayName(sortBy: SortOption, sortOrder: SortOrder): String {
        return when (sortBy) {
            SortOption.NONE -> "None"
            SortOption.TITLE -> if (sortOrder == SortOrder.ASC) "Title (A-Z)" else "Title (Z-A)"
            SortOption.YEAR -> if (sortOrder == SortOrder.ASC) "Year (Old)" else "Year (New)"
            SortOption.RATING -> if (sortOrder == SortOrder.ASC) "Rating (Low)" else "Rating (High)"
        }
    }
}