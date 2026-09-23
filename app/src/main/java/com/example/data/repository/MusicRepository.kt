package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MusicRepository(
    private val dao: MusicDao,
    private val scope: CoroutineScope
) {

    init {
        scope.launch(Dispatchers.IO) {
            initDemoDataIfNeeded()
        }
    }

    private suspend fun initDemoDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingSongs = dao.getAllSongs().first()
        if (existingSongs.isEmpty()) {
            dao.insertArtists(InitialData.artists)
            dao.insertAlbums(InitialData.albums)
            dao.insertSongs(InitialData.songs)
            dao.insertPlaylists(InitialData.playlists)
            for (crossRef in InitialData.playlistSongs) {
                dao.addSongToPlaylist(crossRef)
            }
            for (fav in InitialData.favorites) {
                dao.insertFavorite(fav)
            }
            // Add a couple of initial recently played
            dao.insertRecentlyPlayed(RecentlyPlayedEntity("song_1", System.currentTimeMillis() - 100000))
            dao.insertRecentlyPlayed(RecentlyPlayedEntity("song_4", System.currentTimeMillis() - 50000))
            dao.insertUsers(InitialData.users)
        }
    }

    val favoriteSongIds: Flow<Set<String>> = dao.getFavoriteSongIds()
        .map { it.toSet() }

    val allSongs: Flow<List<Song>> = combine(dao.getAllSongs(), favoriteSongIds) { songs, favIds ->
        songs.map { it.toModel(isFav = favIds.contains(it.id)) }
    }

    val favoriteSongs: Flow<List<Song>> = combine(dao.getFavoriteSongs(), favoriteSongIds) { songs, favIds ->
        songs.map { it.toModel(isFav = true) }
    }

    val recentlyPlayedSongs: Flow<List<Song>> = combine(dao.getRecentlyPlayedSongs(), favoriteSongIds) { songs, favIds ->
        songs.map { it.toModel(isFav = favIds.contains(it.id)) }
    }

    val allArtists: Flow<List<Artist>> = dao.getAllArtists().map { list ->
        list.map { it.toModel() }
    }

    val allAlbums: Flow<List<Album>> = dao.getAllAlbums().map { list ->
        list.map { it.toModel() }
    }

    val allPlaylists: Flow<List<Playlist>> = dao.getAllPlaylists().map { list ->
        list.map { it.toModel() }
    }

    val activeUser: Flow<UserProfile?> = dao.getActiveUser().map { it?.toModel() }

    val allUsers: Flow<List<UserProfile>> = dao.getAllUsers().map { list ->
        list.map { it.toModel() }
    }

    suspend fun toggleFavorite(songId: String) = withContext(Dispatchers.IO) {
        val count = dao.isFavorite(songId)
        if (count > 0) {
            dao.deleteFavorite(songId)
        } else {
            dao.insertFavorite(FavoriteEntity(songId = songId, addedAt = System.currentTimeMillis()))
        }
    }

    suspend fun recordRecentlyPlayed(songId: String) = withContext(Dispatchers.IO) {
        dao.insertRecentlyPlayed(RecentlyPlayedEntity(songId, System.currentTimeMillis()))
        dao.incrementPlayCount(songId)
    }

    suspend fun createPlaylist(title: String, description: String, coverUrl: String): String = withContext(Dispatchers.IO) {
        val id = "pl_${UUID.randomUUID().toString().take(8)}"
        val playlist = PlaylistEntity(
            id = id,
            title = title,
            description = description,
            coverUrl = coverUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80" },
            isCustom = true,
            createdAt = System.currentTimeMillis()
        )
        dao.insertPlaylist(playlist)
        id
    }

    suspend fun updatePlaylist(id: String, title: String, description: String) = withContext(Dispatchers.IO) {
        dao.getPlaylistById(id).first()?.let { current ->
            dao.updatePlaylist(current.copy(title = title, description = description))
        }
    }

    suspend fun deletePlaylist(id: String) = withContext(Dispatchers.IO) {
        dao.deletePlaylistById(id)
        dao.clearPlaylistSongs(id)
    }

    suspend fun addSongToPlaylist(playlistId: String, songId: String) = withContext(Dispatchers.IO) {
        dao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId, System.currentTimeMillis().toInt()))
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) = withContext(Dispatchers.IO) {
        dao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongsForPlaylist(playlistId: String): Flow<List<Song>> =
        combine(dao.getSongsForPlaylist(playlistId), favoriteSongIds) { songs, favIds ->
            songs.map { it.toModel(isFav = favIds.contains(it.id)) }
        }

    fun getPlaylistById(playlistId: String): Flow<Playlist?> =
        dao.getPlaylistById(playlistId).map { it?.toModel() }

    fun getArtistById(artistId: String): Flow<Artist?> =
        dao.getArtistById(artistId).map { it?.toModel() }

    fun getSongsByArtist(artistId: String): Flow<List<Song>> =
        combine(dao.getSongsByArtist(artistId), favoriteSongIds) { songs, favIds ->
            songs.map { it.toModel(isFav = favIds.contains(it.id)) }
        }

    fun getAlbumsByArtist(artistId: String): Flow<List<Album>> =
        dao.getAlbumsByArtist(artistId).map { list -> list.map { it.toModel() } }

    fun getAlbumById(albumId: String): Flow<Album?> =
        dao.getAlbumById(albumId).map { it?.toModel() }

    fun getSongsByAlbum(albumId: String): Flow<List<Song>> =
        combine(dao.getSongsByAlbum(albumId), favoriteSongIds) { songs, favIds ->
            songs.map { it.toModel(isFav = favIds.contains(it.id)) }
        }

    suspend fun toggleFollowArtist(artistId: String, currentFollowed: Boolean) = withContext(Dispatchers.IO) {
        dao.updateArtistFollowStatus(artistId, !currentFollowed)
    }

    suspend fun toggleSaveAlbum(albumId: String, currentSaved: Boolean) = withContext(Dispatchers.IO) {
        dao.updateAlbumSavedStatus(albumId, !currentSaved)
    }

    suspend fun addSong(song: Song) = withContext(Dispatchers.IO) {
        dao.insertSong(song.toEntity())
    }

    suspend fun updateSong(song: Song) = withContext(Dispatchers.IO) {
        dao.updateSong(song.toEntity())
    }

    suspend fun deleteSong(songId: String) = withContext(Dispatchers.IO) {
        dao.deleteSongById(songId)
    }

    suspend fun addArtist(artist: Artist) = withContext(Dispatchers.IO) {
        dao.insertArtist(artist.toEntity())
    }

    suspend fun deleteArtist(artistId: String) = withContext(Dispatchers.IO) {
        dao.deleteArtistById(artistId)
    }

    suspend fun addAlbum(album: Album) = withContext(Dispatchers.IO) {
        dao.insertAlbum(album.toEntity())
    }

    suspend fun deleteAlbum(albumId: String) = withContext(Dispatchers.IO) {
        dao.deleteAlbumById(albumId)
    }

    suspend fun switchUser(userId: String) = withContext(Dispatchers.IO) {
        dao.deactivateAllUsers()
        dao.activateUser(userId)
    }

    suspend fun createUser(username: String, displayName: String, email: String, isAdmin: Boolean) = withContext(Dispatchers.IO) {
        val id = "user_${UUID.randomUUID().toString().take(6)}"
        dao.deactivateAllUsers()
        val user = UserEntity(
            id = id,
            username = username,
            displayName = displayName,
            email = email,
            isAdmin = isAdmin,
            isActive = true
        )
        dao.insertUser(user)
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        dao.deleteUser(userId)
    }
}

// Mapper extension functions
private fun SongEntity.toModel(isFav: Boolean): Song = Song(
    id = id,
    title = title,
    artistId = artistId,
    artistName = artistName,
    albumId = albumId,
    albumTitle = albumTitle,
    durationMs = durationMs,
    audioUrl = audioUrl,
    coverUrl = coverUrl,
    genre = genre,
    isFavorite = isFav,
    playCount = playCount
)

private fun Song.toEntity(): SongEntity = SongEntity(
    id = id,
    title = title,
    artistId = artistId,
    artistName = artistName,
    albumId = albumId,
    albumTitle = albumTitle,
    durationMs = durationMs,
    audioUrl = audioUrl,
    coverUrl = coverUrl,
    genre = genre,
    playCount = playCount
)

private fun ArtistEntity.toModel(): Artist = Artist(
    id = id,
    name = name,
    bio = bio,
    imageUrl = imageUrl,
    monthlyListeners = monthlyListeners,
    isFollowed = isFollowed
)

private fun Artist.toEntity(): ArtistEntity = ArtistEntity(
    id = id,
    name = name,
    bio = bio,
    imageUrl = imageUrl,
    monthlyListeners = monthlyListeners,
    isFollowed = isFollowed
)

private fun AlbumEntity.toModel(): Album = Album(
    id = id,
    title = title,
    artistId = artistId,
    artistName = artistName,
    coverUrl = coverUrl,
    releaseYear = releaseYear,
    isSaved = isSaved
)

private fun Album.toEntity(): AlbumEntity = AlbumEntity(
    id = id,
    title = title,
    artistId = artistId,
    artistName = artistName,
    coverUrl = coverUrl,
    releaseYear = releaseYear,
    isSaved = isSaved
)

private fun PlaylistEntity.toModel(): Playlist = Playlist(
    id = id,
    title = title,
    description = description,
    coverUrl = coverUrl,
    isCustom = isCustom
)

private fun UserEntity.toModel(): UserProfile = UserProfile(
    id = id,
    username = username,
    displayName = displayName,
    email = email,
    isAdmin = isAdmin
)
