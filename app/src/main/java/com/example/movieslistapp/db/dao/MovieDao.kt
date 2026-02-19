package com.example.movieslistapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieslistapp.db.entity.MovieDetailsEntity
import com.example.movieslistapp.db.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE `query` = :searchQuery")
    suspend fun getMoviesByQuery(searchQuery: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(details: MovieDetailsEntity)

    @Query("SELECT * FROM movie_details WHERE imdbID = :imdbId")
    suspend fun getMovieDetails(imdbId: String): MovieDetailsEntity?
}