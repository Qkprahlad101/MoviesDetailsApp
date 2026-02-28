package com.example.movieslistapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.movieslistapp.db.entity.MovieDetailsEntity
import com.example.movieslistapp.db.entity.MovieEntity
import com.example.movieslistapp.db.entity.MovieInteractionEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE `query` = :searchQuery")
    suspend fun getMoviesByQuery(searchQuery: String): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE `query` = :searchQuery LIMIT :limit OFFSET :offset")
    suspend fun getMoviesByQueryPaginated(searchQuery: String, limit: Int, offset: Int): List<MovieEntity>

    @Query("SELECT COUNT(*) FROM movies WHERE `query` = :searchQuery")
    suspend fun getMoviesCountByQuery(searchQuery: String): Int

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

    @Query("SELECT DISTINCT substr(trim(Genre), 1, instr(trim(Genre) || ',', ',') - 1) as genre FROM movie_details WHERE Genre IS NOT NULL AND Genre != ''")
    suspend fun getAllGenres(): List<String>

    @Query("""
        SELECT * FROM movie_details 
        WHERE Genre LIKE '%' || :genre || '%' 
        AND imdbRating IS NOT NULL 
        ORDER BY CAST(imdbRating AS REAL) DESC 
        LIMIT 10
    """)
    suspend fun getTopRatedMoviesByGenre(genre: String): List<MovieDetailsEntity>

    @Query("SELECT * FROM movie_details WHERE imdbRating IS NOT NULL ORDER BY CAST(imdbRating AS REAL) DESC LIMIT 10")
    suspend fun getTopRatedMoviesOverall(): List<MovieDetailsEntity>

    @Query("UPDATE movies SET trailer = :trailerUrl WHERE imdbId = :imdbId")
    suspend fun updateMovieTrailer(imdbId: String, trailerUrl: String)

    @Query("UPDATE movie_details SET trailer = :trailerUrl WHERE imdbID = :imdbId")
    suspend fun updateMovieDetailsTrailer(imdbId: String, trailerUrl: String)

    @Query("SELECT trailer FROM movies WHERE imdbId = :imdbId")
    suspend fun getMovieTrailer(imdbId: String): String?

    @Query("SELECT trailer FROM movie_details WHERE imdbID = :imdbId")
    suspend fun getMovieDetailsTrailer(imdbId: String): String?

    @Query("SELECT * FROM movie_details ORDER BY year DESC LIMIT 10")
    fun getRecentlyAddedMovies(): List<MovieDetailsEntity>

    // Movie Interaction tracking
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateInteraction(interaction: MovieInteractionEntity)

    @Query("SELECT * FROM movie_interactions WHERE imdbId = :imdbId")
    suspend fun getInteraction(imdbId: String): MovieInteractionEntity?

    @Transaction
    suspend fun incrementOpenCount(imdbId: String) {
        val current = getInteraction(imdbId)
        if (current == null) {
            insertOrUpdateInteraction(MovieInteractionEntity(imdbId, 1))
        } else {
            insertOrUpdateInteraction(current.copy(
                openCount = current.openCount + 1,
                lastOpenedTimestamp = System.currentTimeMillis()
            ))
        }
    }
}
