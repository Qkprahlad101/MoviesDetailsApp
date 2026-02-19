package com.example.movieslistapp.domain.mapper

import com.example.movieslistapp.data.model.Movie
import com.example.movieslistapp.data.model.MovieDetails
import com.example.movieslistapp.db.entity.MovieDetailsEntity
import com.example.movieslistapp.db.entity.MovieEntity

fun Movie.toMovieEntity(query: String) = MovieEntity(
    imdbId = imdbID,
    query = query,
    Title = Title,
    Year = Year,
    Poster = Poster
)

fun MovieEntity.toMovie() = Movie(
    imdbID = imdbId,
    Title = Title,
    Year = Year,
    Poster = Poster
)

fun MovieDetails.toMovieDetailsEntity() = MovieDetailsEntity(
    imdbID = imdbID,
    Title = Title,
    Year = Year,
    Rated = Rated,
    Released = Released,
    Runtime = Runtime,
    Genre = Genre,
    Director = Director,
    Writer = Writer,
    Actors = Actors,
    Plot = Plot,
    Language = Language,
    Country = Country,
    Awards = Awards,
    Poster = Poster,
    Ratings = Ratings,
    Metascore = Metascore,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    Type = Type,
    DVD = DVD,
    BoxOffice = BoxOffice,
    Production = Production,
    Website = Website,
    Response = Response
)

fun MovieDetailsEntity.toMovieDetails() = MovieDetails(
    imdbID = imdbID,
    Title = Title,
    Year = Year,
    Rated = Rated,
    Released = Released,
    Runtime = Runtime,
    Genre = Genre,
    Director = Director,
    Writer = Writer,
    Actors = Actors,
    Plot = Plot,
    Language = Language,
    Country = Country,
    Awards = Awards,
    Poster = Poster,
    Ratings = Ratings,
    Metascore = Metascore,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    Type = Type,
    DVD = DVD,
    BoxOffice = BoxOffice,
    Production = Production,
    Website = Website,
    Response = Response
)