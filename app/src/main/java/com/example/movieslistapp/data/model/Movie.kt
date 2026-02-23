package com.example.movieslistapp.data.model

data class Movie(
    val Title: String,
    val Year: String,
    val Poster: String,
    val imdbID: String,
    val trailer: String? = null
)

data class MovieDetails(
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
    val imdbID: String,
    val Type: String,
    val DVD: String?,
    val BoxOffice: String?,
    val Production: String?,
    val Website: String?,
    val Response: String,
    val trailer: String? = null
)

data class Rating(
    val Source: String,
    val Value: String
)
data class MovieResponse(
    val Search: List<Movie>?,
    val Response: String,
    val Error: String?
)
