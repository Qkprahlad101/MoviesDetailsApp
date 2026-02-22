package com.example.trailer_player

object YoutubeUtils {
    fun extractVideoId(url: String): String? {
        val patterns = listOf(
            "(?:v=|\\/v\\/|\\/embed\\/|youtu\\.be\\/|\\/shorts\\/|\\/watch\\?v=|&v=)([a-zA-Z0-9_-]{11})".toRegex()
        )
        for (pattern in patterns) {
            val matchResult = pattern.find(url)
            if (matchResult != null) {
                return matchResult.groupValues[1]
            }
        }
        return null
    }
}