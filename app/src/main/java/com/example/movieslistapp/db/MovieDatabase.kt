package com.example.movieslistapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.movieslistapp.db.converter.Converters
import com.example.movieslistapp.db.dao.MovieDao
import com.example.movieslistapp.db.entity.MovieDetailsEntity
import com.example.movieslistapp.db.entity.MovieEntity
import com.example.movieslistapp.db.entity.MovieInteractionEntity

@Database(
    entities = [MovieEntity::class, MovieDetailsEntity::class, MovieInteractionEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object{
        val MIGRATION_1_2 = object : Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //add timestamp column to movies table
                db.execSQL("ALTER TABLE movies ADD COLUMN timestamp INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")

                //add timestamp column to movie_details table
                db.execSQL("ALTER TABLE movie_details ADD COLUMN timestamp INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
            }
        }

        val MIGRATION_2_3 = object : Migration(2,3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //add trailer column to movies table
                db.execSQL("ALTER TABLE movies ADD COLUMN trailer TEXT")
                //add trailer column to movie_details table
                db.execSQL("ALTER TABLE movie_details ADD COLUMN trailer TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS movie_interactions (
                        imdbId TEXT NOT NULL, 
                        openCount INTEGER NOT NULL, 
                        lastOpenedTimestamp INTEGER NOT NULL, 
                        PRIMARY KEY(imdbId)
                    )
                """.trimIndent())
            }
        }
    }
}
