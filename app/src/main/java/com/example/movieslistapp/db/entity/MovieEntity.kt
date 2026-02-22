package com.example.movieslistapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity (
    @PrimaryKey val imdbId: String,
    val query: String,
    val Title: String,
    val Year: String,
    val Poster: String,
    val timestamp: Long = System.currentTimeMillis(),
    val trailer: String? = null
)
