package com.example.movieslistapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieslistapp.db.converter.Converters
import com.example.movieslistapp.db.dao.MovieDao
import com.example.movieslistapp.db.entity.MovieDetailsEntity
import com.example.movieslistapp.db.entity.MovieEntity

@Database(
    entities = [MovieEntity::class, MovieDetailsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}