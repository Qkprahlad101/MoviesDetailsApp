package com.example.movieslistapp.data.repository

import android.util.Log
import com.example.aitrailersdk.core.model.TrailerRequest
import com.example.aitrailersdk.core.service.MovieValidator
import com.example.movieslistapp.data.ApiService
import kotlinx.coroutines.withTimeoutOrNull

class AiMovieValidator(
    private val apiService: ApiService
) : MovieValidator {
    override suspend fun validateAndGetDetails(title: String): TrailerRequest? {
        return try {
            // Reduced internal timeout to give the overall process more breathing room
            withTimeoutOrNull(8000) {
                val movie = apiService.getMovieByTitle(title)
                if (movie.Response == "True") {
                    TrailerRequest(
                        movieTitle = movie.Title,
                        year = movie.Year,
                        description = "IMDB_ID:${movie.imdbID}"
                    )
                } else null
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            // If the outer scope timed out (the 20s error you see),
            // we log it and return null to allow the partial list to be returned
            Log.e("AiMovieValidator", "Validation cancelled/timed out for $title")
            null
        } catch (e: Exception) {
            Log.e("AiMovieValidator", "Error validating $title", e)
            null
        }
    }
}
