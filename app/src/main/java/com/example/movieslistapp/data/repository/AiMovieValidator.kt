package com.example.movieslistapp.data.repository

import android.util.Log
import com.example.aitrailersdk.core.model.TrailerRequest
import com.example.aitrailersdk.core.service.MovieValidator
import com.example.movieslistapp.data.ApiService

class AiMovieValidator(
    private val apiService: ApiService
) : MovieValidator {
    override suspend fun validateAndGetDetails(title: String): TrailerRequest? {
        return try {
            val response = apiService.getMoviesByTitle(title, 1)
            Log.d("AiMovieValidator", "validateAndGetDetails, movie: ${response}")
            if (response.Response == "True" && !response.Search.isNullOrEmpty()) {
                val movie = response.Search[0]
                // We store the imdbID in the description field with a prefix
                // This allows us to retrieve it later without another network search
                Log.d("AiMovieValidator", "validateAndGetDetails, movie: ${movie}")
                TrailerRequest(
                    movieTitle = movie.Title,
                    year = movie.Year,
                    description = "IMDB_ID:${movie.imdbID}"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
