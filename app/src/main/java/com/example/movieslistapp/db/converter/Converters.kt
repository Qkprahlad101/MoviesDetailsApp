package com.example.movieslistapp.db.converter

import androidx.room.TypeConverter
import com.example.movieslistapp.data.model.Rating
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    @TypeConverter
    fun fromRatingList(ratings: List<Rating>): String {
        return gson.toJson(ratings)
    }

    @TypeConverter
    fun toRatingList(ratingsString: String): List<Rating> {
        return gson.fromJson(ratingsString, object: TypeToken<List<Rating>>() {}.type)
    }
}
