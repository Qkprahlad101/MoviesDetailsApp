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

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query("SELECT COUNT(*) FROM movie_details")
    suspend fun getMovieDetailsCount(): Int

    @Query("DELETE FROM movies WHERE imdbID IN (SELECT imdbID FROM movies ORDER BY timestamp ASC LIMIT :limit)")
    suspend fun deleteOldestMovies(limit: Int)

    @Query("DELETE FROM movie_details WHERE imdbID IN (SELECT imdbID FROM movie_details ORDER BY timestamp ASC LIMIT :limit)")
    suspend fun deleteOldestMovieDetails(limit: Int)
}