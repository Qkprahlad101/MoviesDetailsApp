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
            // Use a timeout to prevent a single validation from hanging the entire suggestion process.
            // 10 seconds is usually enough for a single OMDb API call.
            withTimeoutOrNull(10000) {
                // Using getMovieByTitle (parameter 't') is more efficient and accurate for validating a specific title
                // suggested by AI compared to a general search (parameter 's').
                val movie = apiService.getMovieByTitle(title)
                Log.d("AiMovieValidator", "validateAndGetDetails, response for '$title': $movie")
                
                if (movie.Response == "True") {
                    Log.d("AiMovieValidator", "validateAndGetDetails, movie found: ${movie.Title} (${movie.Year})")
                    
                    // We store the imdbID in the description field with a prefix
                    // This allows us to retrieve it later without another network search
                    TrailerRequest(
                        movieTitle = movie.Title,
                        year = movie.Year,
                        description = "IMDB_ID:${movie.imdbID}"
                    )
                } else {
                    Log.d("AiMovieValidator", "validateAndGetDetails, no movie found for: $title")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("AiMovieValidator", "validateAndGetDetails, error validating $title", e)
            null
        }
    }
}
