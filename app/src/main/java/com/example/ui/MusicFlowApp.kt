package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Song
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Search : Screen()
    object Library : Screen()
    object Profile : Screen()
    data class PlaylistDetail(val playlistId: String) : Screen()
    data class ArtistDetail(val artistId: String) : Screen()
    data class AlbumDetail(val albumId: String) : Screen()
    object AdminDashboard : Screen()
}

@Composable
fun MusicFlowApp(
    viewModel: MusicViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showFullPlayer by remember { mutableStateOf(false) }

    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    // Reactive states
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val allArtists by viewModel.allArtists.collectAsStateWithLifecycle()
    val allAlbums by viewModel.allAlbums.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (!isWideScreen) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Persistent Mini Player on Phone
                        if (playbackState.currentSong != null) {
                            MiniPlayer(
                                playbackState = playbackState,
                                onPlayPauseClick = { viewModel.playerController.togglePlayPause() },
                                onNextClick = { viewModel.playerController.playNext() },
                                onFavoriteClick = { songId ->
                                    viewModel.toggleFavorite(songId)
                                    coroutineScope.launch {
                                        val isFav = favoriteSongs.any { it.id == songId }
                                        snackbarHostState.showSnackbar(
                                            if (isFav) "Removed from Liked Songs" else "Added to Liked Songs"
                                        )
                                    }
                                },
                                onExpandClick = { showFullPlayer = true }
                            )
                        }

                        NavigationBar(
                            containerColor = DarkSurface,
                            contentColor = TextPrimary,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = currentScreen is Screen.Home,
                                onClick = { currentScreen = Screen.Home },
                                icon = {
                                    Icon(
                                        if (currentScreen is Screen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = { Text("Home") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = FlowCyan,
                                    indicatorColor = FlowCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_home")
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Search,
                                onClick = { currentScreen = Screen.Search },
                                icon = {
                                    Icon(
                                        if (currentScreen is Screen.Search) Icons.Filled.Search else Icons.Outlined.Search,
                                        contentDescription = "Search"
                                    )
                                },
                                label = { Text("Search") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = FlowCyan,
                                    indicatorColor = FlowCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_search")
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Library || currentScreen is Screen.PlaylistDetail,
                                onClick = { currentScreen = Screen.Library },
                                icon = {
                                    Icon(
                                        if (currentScreen is Screen.Library) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                                        contentDescription = "Library"
                                    )
                                },
                                label = { Text("Library") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = FlowCyan,
                                    indicatorColor = FlowCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_library")
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Profile || currentScreen is Screen.AdminDashboard,
                                onClick = { currentScreen = Screen.Profile },
                                icon = {
                                    Icon(
                                        if (currentScreen is Screen.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                        contentDescription = "Profile"
                                    )
                                },
                                label = { Text("Profile") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = FlowCyan,
                                    indicatorColor = FlowCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag("nav_profile")
                            )
                        }
                    }
                }
            },
            containerColor = DarkBackground
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Wide Screen Navigation Rail (Tablet / Desktop mode)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = DarkSurface,
                        contentColor = TextPrimary,
                        header = {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(42.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.GraphicEq,
                                    contentDescription = "MUSIC FLOW",
                                    tint = FlowCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    ) {
                        NavigationRailItem(
                            selected = currentScreen is Screen.Home,
                            onClick = { currentScreen = Screen.Home },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = FlowCyan
                            )
                        )
                        NavigationRailItem(
                            selected = currentScreen is Screen.Search,
                            onClick = { currentScreen = Screen.Search },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = FlowCyan
                            )
                        )
                        NavigationRailItem(
                            selected = currentScreen is Screen.Library,
                            onClick = { currentScreen = Screen.Library },
                            icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                            label = { Text("Library") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = FlowCyan
                            )
                        )
                        NavigationRailItem(
                            selected = currentScreen is Screen.Profile,
                            onClick = { currentScreen = Screen.Profile },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                indicatorColor = FlowCyan
                            )
                        )
                    }
                }

                // Main Content Pane
                Box(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            when (val screen = currentScreen) {
                                is Screen.Home -> {
                                    HomeScreen(
                                        songs = allSongs,
                                        recentlyPlayed = recentlyPlayed,
                                        playlists = allPlaylists,
                                        albums = allAlbums,
                                        artists = allArtists,
                                        activeUser = activeUser,
                                        playbackState = playbackState,
                                        onSongPlay = { song, queue ->
                                            viewModel.playSong(song, queue)
                                        },
                                        onFavoriteToggle = { songId ->
                                            viewModel.toggleFavorite(songId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Updated favorites")
                                            }
                                        },
                                        onAddToPlaylist = { song ->
                                            songToAddToPlaylist = song
                                        },
                                        onAddToQueue = { song ->
                                            viewModel.playerController.addToQueue(song)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added \"${song.title}\" to queue")
                                            }
                                        },
                                        onPlaylistClick = { id ->
                                            currentScreen = Screen.PlaylistDetail(id)
                                        },
                                        onArtistClick = { id ->
                                            currentScreen = Screen.ArtistDetail(id)
                                        },
                                        onAlbumClick = { id ->
                                            currentScreen = Screen.AlbumDetail(id)
                                        },
                                        onNavigateToSearch = { currentScreen = Screen.Search },
                                        onNavigateToProfile = { currentScreen = Screen.Profile }
                                    )
                                }

                                is Screen.Search -> {
                                    SearchScreen(
                                        songs = allSongs,
                                        artists = allArtists,
                                        albums = allAlbums,
                                        playlists = allPlaylists,
                                        playbackState = playbackState,
                                        onSongPlay = { song, queue ->
                                            viewModel.playSong(song, queue)
                                        },
                                        onFavoriteToggle = { songId ->
                                            viewModel.toggleFavorite(songId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Updated favorites")
                                            }
                                        },
                                        onAddToPlaylist = { song ->
                                            songToAddToPlaylist = song
                                        },
                                        onAddToQueue = { song ->
                                            viewModel.playerController.addToQueue(song)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added \"${song.title}\" to queue")
                                            }
                                        },
                                        onPlaylistClick = { id ->
                                            currentScreen = Screen.PlaylistDetail(id)
                                        },
                                        onArtistClick = { id ->
                                            currentScreen = Screen.ArtistDetail(id)
                                        },
                                        onAlbumClick = { id ->
                                            currentScreen = Screen.AlbumDetail(id)
                                        }
                                    )
                                }

                                is Screen.Library -> {
                                    LibraryScreen(
                                        favoriteSongs = favoriteSongs,
                                        recentlyPlayed = recentlyPlayed,
                                        playlists = allPlaylists,
                                        albums = allAlbums,
                                        artists = allArtists,
                                        playbackState = playbackState,
                                        onSongPlay = { song, queue ->
                                            viewModel.playSong(song, queue)
                                        },
                                        onFavoriteToggle = { songId ->
                                            viewModel.toggleFavorite(songId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Updated favorites")
                                            }
                                        },
                                        onAddToPlaylist = { song ->
                                            songToAddToPlaylist = song
                                        },
                                        onAddToQueue = { song ->
                                            viewModel.playerController.addToQueue(song)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added \"${song.title}\" to queue")
                                            }
                                        },
                                        onPlaylistClick = { id ->
                                            currentScreen = Screen.PlaylistDetail(id)
                                        },
                                        onArtistClick = { id ->
                                            currentScreen = Screen.ArtistDetail(id)
                                        },
                                        onAlbumClick = { id ->
                                            currentScreen = Screen.AlbumDetail(id)
                                        },
                                        onCreatePlaylistClick = { showCreatePlaylistDialog = true },
                                        onDeletePlaylist = { id ->
                                            viewModel.deletePlaylist(id)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Playlist deleted")
                                            }
                                        }
                                    )
                                }

                                is Screen.PlaylistDetail -> {
                                    val playlist by viewModel.getPlaylistById(screen.playlistId)
                                        .collectAsStateWithLifecycle(initialValue = null)
                                    val playlistSongs by viewModel.getSongsForPlaylist(screen.playlistId)
                                        .collectAsStateWithLifecycle(initialValue = emptyList())

                                    PlaylistDetailScreen(
                                        playlist = playlist,
                                        songs = playlistSongs,
                                        playbackState = playbackState,
                                        onBackClick = { currentScreen = Screen.Library },
                                        onSongPlay = { song, queue ->
                                            viewModel.playSong(song, queue)
                                        },
                                        onFavoriteToggle = { songId ->
                                            viewModel.toggleFavorite(songId)
                                        },
                                        onRemoveSongFromPlaylist = { songId ->
                                            viewModel.removeSongFromPlaylist(screen.playlistId, songId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Removed from playlist")
                                            }
                                        },
                                        onRenamePlaylist = { newName ->
                                            viewModel.renamePlaylist(screen.playlistId, newName)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Playlist renamed to \"$newName\"")
                                            }
                                        },
                                        onDeletePlaylist = {
                                            viewModel.deletePlaylist(screen.playlistId)
                                            currentScreen = Screen.Library
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Playlist deleted")
                                            }
                                        }
                                    )
                                }

                                is Screen.ArtistDetail -> {
                                    val artist by viewModel.getArtistById(screen.artistId)
                                        .collectAsStateWithLifecycle(initialValue = null)
                                    val artistSongs by viewModel.getSongsByArtist(screen.artistId)
                                        .collectAsStateWithLifecycle(initialValue = emptyList())
                                    val artistAlbums by viewModel.getAlbumsByArtist(screen.artistId)
                                        .collectAsStateWithLifecycle(initialValue = emptyList())

                                    ArtistDetailScreen(
                                        artist = artist,
                                        songs = artistSongs,
                                        albums = artistAlbums,
                                        playbackState = playbackState,
                                        onBackClick = { currentScreen = Screen.Home },
                                        onSongPlay = { song, queue ->
                                            viewModel.playSong(song, queue)
                                        },
                                        onFavoriteToggle = { songId ->
                                            viewModel.toggleFavorite(songId)
                                        },
                                        onFollowToggle = {
                                            artist?.let {
                                                viewModel.toggleFollowArtist(it.id, it.isFollowed)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (it.isFollowed) "Unfollowed ${it.name}" else "Following ${it.name}"
                                                    )
                                                }
                                            }
                                        },
                                        onAlbumClick = { albumId ->
                                            currentScreen = Screen.AlbumDetail(albumId)
                                        }
                                    )
                                }

                                is Screen.AlbumDetail -> {
                                    val album by viewModel.getAlbumById(screen.albumId)
                                        .collectAsStateWithLifecycle(initialValue = null)
                                    val albumSongs by viewModel.getSongsByAlbum(screen.albumId)
                                        .collectAsStateWithLifecycle(initialValue = emptyList())

                                    AlbumDetailScreen(
                                        album = album,
                                        songs = albumSongs,
                                        playbackState = playbackState,
                                        onBackClick = { currentScreen = Screen.Home },
                                        onSongPlay = { song, queue ->
                                            viewModel.playSong(song, queue)
                                        },
                                        onFavoriteToggle = { songId ->
                                            viewModel.toggleFavorite(songId)
                                        },
                                        onSaveToggle = {
                                            album?.let {
                                                viewModel.toggleSaveAlbum(it.id, it.trackCount > 0)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Updated saved albums")
                                                }
                                            }
                                        }
                                    )
                                }

                                is Screen.Profile -> {
                                    ProfileScreen(
                                        user = activeUser,
                                        allUsers = allUsers,
                                        favoritesCount = favoriteSongs.size,
                                        playlistsCount = allPlaylists.size,
                                        onSwitchUser = { userId ->
                                            viewModel.switchUser(userId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Switched profile session")
                                            }
                                        },
                                        onCreateUser = { username, name, email, isAdmin ->
                                            viewModel.createUser(username, name, email, isAdmin)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Profile created for $name")
                                            }
                                        },
                                        onNavigateToAdmin = {
                                            currentScreen = Screen.AdminDashboard
                                        }
                                    )
                                }

                                is Screen.AdminDashboard -> {
                                    AdminDashboardScreen(
                                        songs = allSongs,
                                        artists = allArtists,
                                        albums = allAlbums,
                                        playlists = allPlaylists,
                                        users = allUsers,
                                        onBackClick = { currentScreen = Screen.Profile },
                                        onAddSong = { song ->
                                            viewModel.addSong(song)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added \"${song.title}\"")
                                            }
                                        },
                                        onUpdateSong = { song ->
                                            viewModel.updateSong(song)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Updated \"${song.title}\"")
                                            }
                                        },
                                        onDeleteSong = { songId ->
                                            viewModel.deleteSong(songId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Deleted track")
                                            }
                                        },
                                        onAddArtist = { artist ->
                                            viewModel.addArtist(artist)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added \"${artist.name}\"")
                                            }
                                        },
                                        onDeleteArtist = { artistId ->
                                            viewModel.deleteArtist(artistId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Deleted artist")
                                            }
                                        },
                                        onAddAlbum = { album ->
                                            viewModel.addAlbum(album)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added \"${album.title}\"")
                                            }
                                        },
                                        onDeleteAlbum = { albumId ->
                                            viewModel.deleteAlbum(albumId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Deleted album")
                                            }
                                        },
                                        onDeleteUser = { userId ->
                                            viewModel.deleteUser(userId)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Deleted user")
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // Wide Screen Mini Player
                        if (isWideScreen && playbackState.currentSong != null) {
                            MiniPlayer(
                                playbackState = playbackState,
                                onPlayPauseClick = { viewModel.playerController.togglePlayPause() },
                                onNextClick = { viewModel.playerController.playNext() },
                                onFavoriteClick = { songId ->
                                    viewModel.toggleFavorite(songId)
                                    coroutineScope.launch {
                                        val isFav = favoriteSongs.any { it.id == songId }
                                        snackbarHostState.showSnackbar(
                                            if (isFav) "Removed from Liked Songs" else "Added to Liked Songs"
                                        )
                                    }
                                },
                                onExpandClick = { showFullPlayer = true }
                            )
                        }
                    }
                }
            }
        }

        // Full Screen Player Modal
        if (showFullPlayer && playbackState.currentSong != null) {
            FullPlayerModal(
                playbackState = playbackState,
                onDismiss = { showFullPlayer = false },
                onPlayPauseClick = { viewModel.playerController.togglePlayPause() },
                onNextClick = { viewModel.playerController.playNext() },
                onPreviousClick = { viewModel.playerController.playPrevious() },
                onSeekTo = { posMs -> viewModel.playerController.seekTo(posMs) },
                onToggleShuffle = { viewModel.playerController.toggleShuffle() },
                onToggleRepeat = { viewModel.playerController.toggleRepeat() },
                onVolumeChange = { vol -> viewModel.playerController.setVolume(vol) },
                onFavoriteClick = { songId ->
                    viewModel.toggleFavorite(songId)
                    coroutineScope.launch {
                        val isFav = favoriteSongs.any { it.id == songId }
                        snackbarHostState.showSnackbar(
                            if (isFav) "Removed from Liked Songs" else "Added to Liked Songs"
                        )
                    }
                },
                onRemoveFromQueue = { idx -> viewModel.playerController.removeFromQueue(idx) },
                onPlayFromQueue = { song -> viewModel.playerController.playSong(song) }
            )
        }

        // Add To Playlist Dialog
        songToAddToPlaylist?.let { song ->
            AddToPlaylistDialog(
                playlists = allPlaylists,
                onDismiss = { songToAddToPlaylist = null },
                onPlaylistSelected = { plId ->
                    viewModel.addSongToPlaylist(plId, song.id)
                    val plName = allPlaylists.find { it.id == plId }?.title ?: "playlist"
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Added \"${song.title}\" to $plName")
                    }
                    songToAddToPlaylist = null
                },
                onCreateNewPlaylist = {
                    showCreatePlaylistDialog = true
                }
            )
        }

        // Create Playlist Dialog
        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { showCreatePlaylistDialog = false },
                onCreate = { title, desc ->
                    viewModel.createPlaylist(title, desc) { newId ->
                        songToAddToPlaylist?.let { song ->
                            viewModel.addSongToPlaylist(newId, song.id)
                        }
                        songToAddToPlaylist = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Created playlist \"$title\"")
                        }
                    }
                }
            )
        }
    }
}
