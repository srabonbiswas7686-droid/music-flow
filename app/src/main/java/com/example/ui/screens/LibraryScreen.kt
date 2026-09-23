package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

enum class LibraryTab(val title: String) {
    LIKED("Liked"),
    PLAYLISTS("Playlists"),
    RECENT("Recent"),
    ARTISTS("Artists"),
    ALBUMS("Albums")
}

@Composable
fun LibraryScreen(
    favoriteSongs: List<Song>,
    recentlyPlayed: List<Song>,
    playlists: List<Playlist>,
    albums: List<Album>,
    artists: List<Artist>,
    playbackState: PlaybackState,
    onSongPlay: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onDeletePlaylist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.LIKED) }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == LibraryTab.PLAYLISTS) {
                FloatingActionButton(
                    onClick = onCreatePlaylistClick,
                    containerColor = FlowCyan,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 80.dp)
                        .testTag("create_playlist_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Playlist")
                }
            }
        },
        containerColor = DarkBackground,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Library",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )

                if (selectedTab != LibraryTab.PLAYLISTS) {
                    IconButton(
                        onClick = onCreatePlaylistClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create", tint = FlowCyan)
                    }
                }
            }

            // Tab bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = DarkBackground,
                contentColor = FlowCyan,
                edgePadding = 20.dp,
                divider = {}
            ) {
                LibraryTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == tab) FlowCyan else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                LibraryTab.LIKED -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 120.dp)
                    ) {
                        item {
                            // Liked banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF8338EC), Color(0xFFFF007F))
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        "Liked Songs",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        "${favoriteSongs.size} tracks saved",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )

                                    if (favoriteSongs.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = { onSongPlay(favoriteSongs.first(), favoriteSongs) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.White,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("play_liked_songs_button")
                                        ) {
                                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Play All", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        if (favoriteSongs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No liked songs yet. Tap the heart icon to save songs!", color = TextSecondary)
                                }
                            }
                        } else {
                            items(favoriteSongs) { song ->
                                SongRowItem(
                                    song = song,
                                    isPlaying = playbackState.isPlaying,
                                    isCurrentSong = playbackState.currentSong?.id == song.id,
                                    onPlayClick = { onSongPlay(song, favoriteSongs) },
                                    onFavoriteClick = { onFavoriteToggle(song.id) },
                                    onAddToPlaylistClick = { onAddToPlaylist(song) },
                                    onAddToQueueClick = { onAddToQueue(song) }
                                )
                            }
                        }
                    }
                }

                LibraryTab.PLAYLISTS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            OutlinedButton(
                                onClick = onCreatePlaylistClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FlowCyan)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create New Playlist", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        items(playlists) { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceVariant)
                                    .clickable { onPlaylistClick(playlist.id) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CoverImage(
                                    url = playlist.coverUrl,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = playlist.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = playlist.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }

                                if (playlist.isCustom) {
                                    IconButton(
                                        onClick = { onDeletePlaylist(playlist.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = FlowPink
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                LibraryTab.RECENT -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 120.dp)
                    ) {
                        if (recentlyPlayed.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No recently played music yet.", color = TextSecondary)
                                }
                            }
                        } else {
                            items(recentlyPlayed) { song ->
                                SongRowItem(
                                    song = song,
                                    isPlaying = playbackState.isPlaying,
                                    isCurrentSong = playbackState.currentSong?.id == song.id,
                                    onPlayClick = { onSongPlay(song, recentlyPlayed) },
                                    onFavoriteClick = { onFavoriteToggle(song.id) },
                                    onAddToPlaylistClick = { onAddToPlaylist(song) },
                                    onAddToQueueClick = { onAddToQueue(song) }
                                )
                            }
                        }
                    }
                }

                LibraryTab.ARTISTS -> {
                    val followed = artists.filter { it.isFollowed }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (followed.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No followed artists yet. Explore and follow your favorites!", color = TextSecondary)
                                }
                            }
                        } else {
                            items(followed) { artist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceVariant)
                                        .clickable { onArtistClick(artist.id) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoverImage(
                                        url = artist.imageUrl,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = artist.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${artist.monthlyListeners / 1000}K monthly listeners",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Followed",
                                        tint = FlowCyan
                                    )
                                }
                            }
                        }
                    }
                }

                LibraryTab.ALBUMS -> {
                    val saved = albums.filter { it.isSaved }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (saved.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No saved albums yet.", color = TextSecondary)
                                }
                            }
                        } else {
                            items(saved) { album ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceVariant)
                                        .clickable { onAlbumClick(album.id) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoverImage(
                                        url = album.coverUrl,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = album.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${album.artistName} • ${album.releaseYear}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Icon(
                                        Icons.Default.Bookmark,
                                        contentDescription = "Saved",
                                        tint = FlowVioletLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
