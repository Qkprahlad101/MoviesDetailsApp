package com.example.movieslistapp.utils

enum class MovieGenre(val displayName: String) {
    // Core / very frequent
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ANIMATION("Animation"),
    BIOGRAPHY("Biography"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FAMILY("Family"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    HORROR("Horror"),
    MUSIC("Music"),
    MUSICAL("Musical"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCI_FI("Sci-Fi"),
    SPORT("Sport"),
    THRILLER("Thriller"),
    WAR("War"),
    WESTERN("Western"),

    // Fairly common sub/additional ones
    FILM_NOIR("Film-Noir"),
    SHORT("Short"),
    NEWS("News"),
    REALITY_TV("Reality-TV"),
    TALK_SHOW("Talk-Show"),
    GAME_SHOW("Game-Show"),

    // Less frequent but still appear
    ADULT("Adult"),
    SUPERHERO("Superhero"),    // sometimes used instead of Action/Adventure

    RECENTLY_ADDED("Recently Added")
    ;

    companion object {
        /**
         * Tries to match a genre string from OMDb (case-insensitive)
         * Returns null if no match.
         */
        fun fromString(genreStr: String?): MovieGenre? {
            if (genreStr.isNullOrBlank()) return null

            val normalized = genreStr.trim().replace(" ", "-").replace("_", "-")
                .replace("science-fiction", "sci-fi", ignoreCase = true)
                .uppercase()

            return entries.find {
                it.name == normalized ||
                        it.displayName.uppercase() == normalized ||
                        it.displayName.replace(" ", "-").uppercase() == normalized
            }
        }

        /**
         * Parse comma-separated Genre string from OMDb into a list of enums
         * Ignores unknown values.
         */
        fun parseList(genreField: String?): List<MovieGenre> {
            if (genreField.isNullOrBlank()) return emptyList()

            return genreField.split(",")
                .mapNotNull { fromString(it.trim()) }
        }
    }
}