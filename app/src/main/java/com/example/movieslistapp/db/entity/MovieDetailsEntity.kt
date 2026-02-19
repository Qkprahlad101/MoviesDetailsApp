package com.example.movieslistapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieslistapp.data.model.Rating


@Entity(tableName = "movie_details")
data class MovieDetailsEntity(
    @PrimaryKey val imdbID: String,
    val Title: String,
    val Year: String,
    val Rated: String?,
    val Released: String?,
    val Runtime: String?,
    val Genre: String?,
    val Director: String?,
    val Writer: String?,
    val Actors: String?,
    val Plot: String?,
    val Language: String?,
    val Country: String?,
    val Awards: String?,
    val Poster: String,
    val Ratings: List<Rating>?,
    val Metascore: String?,
    val imdbRating: String?,
    val imdbVotes: String?,
    val Type: String,
    val DVD: String?,
    val BoxOffice: String?,
    val Production: String?,
    val Website: String?,
    val Response: String
)
