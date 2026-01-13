package com.example.movieslistapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("imdbID") val imdbID: String,
    @SerializedName("Title")  val title: String,
    @SerializedName("Year")   val year: String,
    @SerializedName("Type")   val type: String,
    @SerializedName("Poster") val poster: String,
    // Details fields (optional for search)
    @SerializedName("Plot")   val plot: String? = null,
    @SerializedName("imdbRating") val imdbRating: String? = null
)

data class MoviesListResponse(
    @SerializedName("Search") val search: List<Movie>?,
    @SerializedName("totalResults") val totalResults: String?,
    @SerializedName("Response") val response: String
)
