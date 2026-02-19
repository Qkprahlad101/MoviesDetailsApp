package com.example.movieslistapp.data.repository

import androidx.collection.LruCache
import com.example.movieslistapp.data.ApiService
import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.data.model.MovieResponse
import com.example.movieslistapp.db.dao.MovieDao
import com.example.movieslistapp.domain.mapper.toMovie
import com.example.movieslistapp.domain.mapper.toMovieDetails
import com.example.movieslistapp.domain.mapper.toMovieDetailsEntity
import com.example.movieslistapp.domain.mapper.toMovieEntity

class GetMoviesRepository(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {

    private val movieDetailsCache = LruCache<String, MovieDetails>(50)
    private val moviesListCache = LruCache<String, MovieResponse>(20)
    suspend fun getMoviesListFromSearch(query: String, currentPage: Int): MovieResponse {

        //check in cache
        moviesListCache.get(query)?.let {
            return it
        }

        //check in db
        val dbMoviesList = movieDao.getMoviesByQuery(query)
        return if (dbMoviesList.isNotEmpty()) {
            MovieResponse(
                Search = dbMoviesList.map { it.toMovie() },
                Error = "",
                Response = "True"
            )
        } else {

            //api call
            val response = apiService.getMoviesListFromSearch(query, currentPage)
            response.Search?.let { movies ->
                movieDao.insertMovies(movies.map { it.toMovieEntity(query) })
            }
            response
        }
    }

    suspend fun getMovieDetails(imdbId: String): MovieDetails {
        //first check in cache and return it
        movieDetailsCache.get(imdbId)?.let {
            return it
        }

        //check database
        val dbDetails = movieDao.getMovieDetails(imdbId)
        return if (dbDetails != null) {
            dbDetails.toMovieDetails()
        } else {
            //api call
            val details = apiService.getMovieDetails(imdbId)
            movieDao.insertMovieDetails(details.toMovieDetailsEntity())
            details
        }
    }
}