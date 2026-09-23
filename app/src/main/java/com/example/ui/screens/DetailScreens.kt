package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist?,
    songs: List<Song>,
    playbackState: PlaybackState,
    onBackClick: () -> Unit,
    onSongPlay: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onRemoveSongFromPlaylist: (String) -> Unit,
    onRenamePlaylist: (String) -> Unit,
    onDeletePlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (playlist == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = FlowCyan)
        }
        return
    }

    var showRenameDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf(playlist.title) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                CoverImage(
                    url = playlist.coverUrl,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    DarkBackground.copy(alpha = 0.8f),
                                    DarkBackground
                                )
                            )
                        )
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Surface(
                        color = FlowCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlowCyan)
                    ) {
                        Text(
                            text = if (playlist.isCustom) "USER PLAYLIST" else "CURATED PLAYLIST",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = FlowCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${playlist.description} • ${songs.size} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FloatingActionButton(
                        onClick = {
                            if (songs.isNotEmpty()) {
                                onSongPlay(songs.first(), songs)
                            }
                        },
                        containerColor = FlowCyan,
                        contentColor = Color.Black,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("play_all_playlist_button")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play All", modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = {
                            if (songs.isNotEmpty()) {
                                val shuffled = songs.shuffled()
                                onSongPlay(shuffled.first(), shuffled)
                            }
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = FlowVioletLight)
                    }
                }

                if (playlist.isCustom) {
                    Row {
                        IconButton(onClick = { showRenameDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename", tint = TextSecondary)
                        }
                        IconButton(onClick = onDeletePlaylist) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = FlowPink)
                        }
                    }
                }
            }
        }

        if (songs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No songs in this playlist yet. Add songs from Search or Home!", color = TextSecondary)
                }
            }
        } else {
            itemsIndexed(songs) { index, song ->
                SongRowItem(
                    song = song,
                    isPlaying = playbackState.isPlaying,
                    isCurrentSong = playbackState.currentSong?.id == song.id,
                    onPlayClick = { onSongPlay(song, songs) },
                    onFavoriteClick = { onFavoriteToggle(song.id) },
                    onAddToPlaylistClick = {},
                    onAddToQueueClick = {},
                    showMenu = false,
                    indexNumber = index + 1,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Playlist", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = FlowCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            onRenamePlaylist(newTitle.trim())
                            showRenameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun ArtistDetailScreen(
    artist: Artist?,
    songs: List<Song>,
    albums: List<Album>,
    playbackState: PlaybackState,
    onBackClick: () -> Unit,
    onSongPlay: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onFollowToggle: () -> Unit,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (artist == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = FlowCyan)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Artist Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                CoverImage(
                    url = artist.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    DarkBackground.copy(alpha = 0.8f),
                                    DarkBackground
                                )
                            )
                        )
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = FlowCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verified Artist", style = MaterialTheme.typography.labelMedium, color = FlowCyan)
                    }
                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${artist.monthlyListeners / 1000}K Monthly Listeners",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        if (songs.isNotEmpty()) {
                            onSongPlay(songs.first(), songs)
                        }
                    },
                    containerColor = FlowCyan,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play All", modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = onFollowToggle,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (artist.isFollowed) FlowCyan else TextTertiary
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (artist.isFollowed) FlowCyan else TextPrimary
                    ),
                    modifier = Modifier.testTag("follow_artist_button")
                ) {
                    Text(if (artist.isFollowed) "Following" else "Follow")
                }
            }
        }

        // Popular Songs
        item {
            Text(
                text = "Popular Songs",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        itemsIndexed(songs) { index, song ->
            SongRowItem(
                song = song,
                isPlaying = playbackState.isPlaying,
                isCurrentSong = playbackState.currentSong?.id == song.id,
                onPlayClick = { onSongPlay(song, songs) },
                onFavoriteClick = { onFavoriteToggle(song.id) },
                indexNumber = index + 1,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
        }

        // Discography
        if (albums.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Discography & Albums",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(albums) { album ->
                        AlbumCard(album = album, onClick = { onAlbumClick(album.id) })
                    }
                }
            }
        }

        // About / Bio
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = artist.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumDetailScreen(
    album: Album?,
    songs: List<Song>,
    playbackState: PlaybackState,
    onBackClick: () -> Unit,
    onSongPlay: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (album == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = FlowCyan)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header Album Art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                CoverImage(
                    url = album.coverUrl,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    DarkBackground.copy(alpha = 0.8f),
                                    DarkBackground
                                )
                            )
                        )
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "ALBUM",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = FlowCyan
                    )
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${album.artistName} • ${album.releaseYear} • ${songs.size} tracks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        if (songs.isNotEmpty()) {
                            onSongPlay(songs.first(), songs)
                        }
                    },
                    containerColor = FlowCyan,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play All", modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (album.trackCount > 0) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Album",
                        tint = FlowVioletLight
                    )
                }
            }
        }

        itemsIndexed(songs) { index, song ->
            SongRowItem(
                song = song,
                isPlaying = playbackState.isPlaying,
                isCurrentSong = playbackState.currentSong?.id == song.id,
                onPlayClick = { onSongPlay(song, songs) },
                onFavoriteClick = { onFavoriteToggle(song.id) },
                indexNumber = index + 1,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
        }
    }
}
