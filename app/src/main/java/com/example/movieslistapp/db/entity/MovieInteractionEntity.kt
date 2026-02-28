package com.example.movieslistapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_interactions")
data class MovieInteractionEntity(
    @PrimaryKey
    val imdbId: String,
    val openCount: Int = 0,
    val lastOpenedTimestamp: Long = System.currentTimeMillis()
)
