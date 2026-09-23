package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MusicFlowDatabase
import com.example.data.model.*
import com.example.data.repository.MusicRepository
import com.example.player.MusicPlayerController
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MusicFlowDatabase.getInstance(application)
    private val repository = MusicRepository(db.musicDao(), viewModelScope)

    val playerController = MusicPlayerController(
        context = application,
        coroutineScope = viewModelScope,
        onSongPlayed = { song ->
            viewModelScope.launch {
                repository.recordRecentlyPlayed(song.id)
            }
        }
    )

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    val allSongs: StateFlow<List<Song>> = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<Song>> = repository.favoriteSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayed: StateFlow<List<Song>> = repository.recentlyPlayedSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allArtists: StateFlow<List<Artist>> = repository.allArtists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlbums: StateFlow<List<Album>> = repository.allAlbums
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlaylists: StateFlow<List<Playlist>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeUser: StateFlow<UserProfile?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserProfile>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun playSong(song: Song, queue: List<Song> = emptyList()) {
        val q = if (queue.isNotEmpty()) queue else listOf(song)
        val idx = q.indexOfFirst { it.id == song.id }.let { if (it >= 0) it else 0 }
        playerController.playQueue(q, idx)
    }

    fun toggleFavorite(songId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(songId)
        }
    }

    fun createPlaylist(title: String, desc: String, coverUrl: String = "", onCreated: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.createPlaylist(title, desc, coverUrl)
            onCreated(id)
        }
    }

    fun renamePlaylist(id: String, title: String) {
        viewModelScope.launch {
            repository.updatePlaylist(id, title, "")
        }
    }

    fun deletePlaylist(id: String) {
        viewModelScope.launch {
            repository.deletePlaylist(id)
        }
    }

    fun addSongToPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun getSongsForPlaylist(playlistId: String): Flow<List<Song>> =
        repository.getSongsForPlaylist(playlistId)

    fun getPlaylistById(playlistId: String): Flow<Playlist?> =
        repository.getPlaylistById(playlistId)

    fun getArtistById(artistId: String): Flow<Artist?> =
        repository.getArtistById(artistId)

    fun getSongsByArtist(artistId: String): Flow<List<Song>> =
        repository.getSongsByArtist(artistId)

    fun getAlbumsByArtist(artistId: String): Flow<List<Album>> =
        repository.getAlbumsByArtist(artistId)

    fun getAlbumById(albumId: String): Flow<Album?> =
        repository.getAlbumById(albumId)

    fun getSongsByAlbum(albumId: String): Flow<List<Song>> =
        repository.getSongsByAlbum(albumId)

    fun toggleFollowArtist(artistId: String, isFollowed: Boolean) {
        viewModelScope.launch {
            repository.toggleFollowArtist(artistId, isFollowed)
        }
    }

    fun toggleSaveAlbum(albumId: String, isSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveAlbum(albumId, isSaved)
        }
    }

    fun addSong(song: Song) {
        viewModelScope.launch {
            repository.addSong(song)
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch {
            repository.updateSong(song)
        }
    }

    fun deleteSong(songId: String) {
        viewModelScope.launch {
            repository.deleteSong(songId)
        }
    }

    fun addArtist(artist: Artist) {
        viewModelScope.launch {
            repository.addArtist(artist)
        }
    }

    fun deleteArtist(artistId: String) {
        viewModelScope.launch {
            repository.deleteArtist(artistId)
        }
    }

    fun addAlbum(album: Album) {
        viewModelScope.launch {
            repository.addAlbum(album)
        }
    }

    fun deleteAlbum(albumId: String) {
        viewModelScope.launch {
            repository.deleteAlbum(albumId)
        }
    }

    fun switchUser(userId: String) {
        viewModelScope.launch {
            repository.switchUser(userId)
        }
    }

    fun createUser(username: String, displayName: String, email: String, isAdmin: Boolean) {
        viewModelScope.launch {
            repository.createUser(username, displayName, email, isAdmin)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerController.releasePlayer()
    }
}
