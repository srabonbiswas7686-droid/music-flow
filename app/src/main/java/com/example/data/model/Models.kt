package com.example.data.model

data class Song(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val albumId: String,
    val albumTitle: String,
    val durationMs: Long,
    val audioUrl: String,
    val coverUrl: String,
    val genre: String,
    val isFavorite: Boolean = false,
    val playCount: Int = 0
)

data class Artist(
    val id: String,
    val name: String,
    val bio: String,
    val imageUrl: String,
    val monthlyListeners: Long,
    val isFollowed: Boolean = false
)

data class Album(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val coverUrl: String,
    val releaseYear: Int,
    val trackCount: Int = 0,
    val isSaved: Boolean = false
)

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val isCustom: Boolean = true,
    val songCount: Int = 0
)

data class UserProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String = "",
    val isAdmin: Boolean = false,
    val favoriteCount: Int = 0,
    val playlistCount: Int = 0,
    val minutesListened: Long = 1420
)

enum class RepeatMode {
    OFF, ALL, ONE
}

data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Song> = emptyList(),
    val queueIndex: Int = -1,
    val volume: Float = 0.85f,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null
)
