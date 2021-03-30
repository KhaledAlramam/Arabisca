package com.sedra.goiptv.data.model

data class MovieInfo(
    val bitrate: String,
    val cast: String,
    val director: String,
    val duration: String,
    val duration_secs: Int,
    val genre: String,
    val movie_image: String,
    val mpaa: String,
    val plot: String,
    val rating: String,
    val releasedate: String,
    val stream_1080p: String,
    val stream_480p: String,
    val stream_4k: String,
    val stream_720p: String,
    val tmdb_id: String,
    val year: String,
    val youtube_trailer: String
)