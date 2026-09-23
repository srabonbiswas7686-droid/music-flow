package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.UUID

enum class AdminTab(val title: String) {
    STATS("Stats"),
    SONGS("Songs"),
    ARTISTS("Artists"),
    ALBUMS("Albums"),
    USERS("Users")
}

@Composable
fun AdminDashboardScreen(
    songs: List<Song>,
    artists: List<Artist>,
    albums: List<Album>,
    playlists: List<Playlist>,
    users: List<UserProfile>,
    onBackClick: () -> Unit,
    onAddSong: (Song) -> Unit,
    onUpdateSong: (Song) -> Unit,
    onDeleteSong: (String) -> Unit,
    onAddArtist: (Artist) -> Unit,
    onDeleteArtist: (String) -> Unit,
    onAddAlbum: (Album) -> Unit,
    onDeleteAlbum: (String) -> Unit,
    onDeleteUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AdminTab.STATS) }

    var showAddSongDialog by remember { mutableStateOf(false) }
    var editingSong by remember { mutableStateOf<Song?>(null) }
    var showAddArtistDialog by remember { mutableStateOf(false) }
    var showAddAlbumDialog by remember { mutableStateOf(false) }

    val totalPlays = remember(songs) { songs.sumOf { it.playCount } }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = "Admin Control Hub",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlowCyan,
                    modifier = Modifier.padding(start = 8.dp)
                )
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
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = DarkSurfaceVariant,
                contentColor = FlowCyan,
                edgePadding = 16.dp,
                divider = {}
            ) {
                AdminTab.values().forEach { tab ->
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

            when (selectedTab) {
                AdminTab.STATS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                "System Metrics & Analytics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard("Total Songs", "${songs.size}", Icons.Default.MusicNote, FlowCyan, Modifier.weight(1f))
                                MetricCard("Total Artists", "${artists.size}", Icons.Default.Person, FlowVioletLight, Modifier.weight(1f))
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard("Total Albums", "${albums.size}", Icons.Default.Album, FlowPink, Modifier.weight(1f))
                                MetricCard("Playlists", "${playlists.size}", Icons.Default.QueueMusic, FlowCyan, Modifier.weight(1f))
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AccentSuccess)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Streams & Engagement", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Total Song Streams: $totalPlays", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text("Registered Users: ${users.size}", color = TextSecondary)
                                    Text("Storage Engine: Room SQLite Local Cache + Real HTTP Streaming", color = TextSecondary)
                                    Text("Stream Protocol: Native Android MediaPlayer with buffering", color = TextSecondary)
                                }
                            }
                        }
                    }
                }

                AdminTab.SONGS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddSongDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add New Track", fontWeight = FontWeight.Bold)
                            }
                        }

                        items(songs) { song ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoverImage(
                                        url = song.coverUrl,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(song.title, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text("${song.artistName} • ${song.genre}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { editingSong = song }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = FlowCyan)
                                    }
                                    IconButton(onClick = { onDeleteSong(song.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = FlowPink)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.ARTISTS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddArtistDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Artist Profile", fontWeight = FontWeight.Bold)
                            }
                        }

                        items(artists) { artist ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoverImage(
                                        url = artist.imageUrl,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(artist.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text("${artist.monthlyListeners / 1000}K monthly listeners", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { onDeleteArtist(artist.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = FlowPink)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.ALBUMS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddAlbumDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Album Record", fontWeight = FontWeight.Bold)
                            }
                        }

                        items(albums) { album ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CoverImage(
                                        url = album.coverUrl,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(album.title, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text("${album.artistName} • ${album.releaseYear}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { onDeleteAlbum(album.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = FlowPink)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminTab.USERS -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(users) { u ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (u.isAdmin) Icons.Default.Security else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (u.isAdmin) FlowVioletLight else FlowCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(u.displayName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text("${u.email} • ${if (u.isAdmin) "Admin" else "Listener"}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (users.size > 1) {
                                        IconButton(onClick = { onDeleteUser(u.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = FlowPink)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Song Dialog
    if (showAddSongDialog) {
        var title by remember { mutableStateOf("") }
        var artistName by remember { mutableStateOf(artists.firstOrNull()?.name ?: "Astral Soundscape") }
        var albumTitle by remember { mutableStateOf(albums.firstOrNull()?.title ?: "Echoes in the Neon") }
        var genre by remember { mutableStateOf("Electronic") }
        var audioUrl by remember { mutableStateOf("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3") }

        AlertDialog(
            onDismissRequest = { showAddSongDialog = false },
            title = { Text("Add New Track", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Track Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = artistName,
                        onValueChange = { artistName = it },
                        label = { Text("Artist Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = albumTitle,
                        onValueChange = { albumTitle = it },
                        label = { Text("Album Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Genre") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = audioUrl,
                        onValueChange = { audioUrl = it },
                        label = { Text("Audio Stream URL (.mp3)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newSong = Song(
                                id = "song_${UUID.randomUUID().toString().take(6)}",
                                title = title.trim(),
                                artistId = artists.firstOrNull { it.name == artistName }?.id ?: "artist_1",
                                artistName = artistName.trim(),
                                albumId = albums.firstOrNull { it.title == albumTitle }?.id ?: "album_1",
                                albumTitle = albumTitle.trim(),
                                durationMs = 280000L,
                                audioUrl = audioUrl.trim(),
                                coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
                                genre = genre.trim()
                            )
                            onAddSong(newSong)
                            showAddSongDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSongDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    // Edit Song Dialog
    editingSong?.let { song ->
        var editTitle by remember { mutableStateOf(song.title) }
        var editGenre by remember { mutableStateOf(song.genre) }

        AlertDialog(
            onDismissRequest = { editingSong = null },
            title = { Text("Edit Track", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editGenre,
                        onValueChange = { editGenre = it },
                        label = { Text("Genre") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSong(song.copy(title = editTitle.trim(), genre = editGenre.trim()))
                        editingSong = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSong = null }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    // Add Artist Dialog
    if (showAddArtistDialog) {
        var name by remember { mutableStateOf("") }
        var bio by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddArtistDialog = false },
            title = { Text("Add Artist Profile", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Artist Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Artist Biography") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newArtist = Artist(
                                id = "artist_${UUID.randomUUID().toString().take(6)}",
                                name = name.trim(),
                                bio = bio.trim().ifBlank { "Exciting new sound innovator on Music Flow." },
                                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                                monthlyListeners = 150000L,
                                isFollowed = false
                            )
                            onAddArtist(newArtist)
                            showAddArtistDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black)
                ) {
                    Text("Add Artist")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddArtistDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    // Add Album Dialog
    if (showAddAlbumDialog) {
        var title by remember { mutableStateOf("") }
        var artistName by remember { mutableStateOf(artists.firstOrNull()?.name ?: "Astral Soundscape") }
        var year by remember { mutableStateOf("2024") }

        AlertDialog(
            onDismissRequest = { showAddAlbumDialog = false },
            title = { Text("Create Album Record", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Album Title") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = artistName,
                        onValueChange = { artistName = it },
                        label = { Text("Artist Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Release Year") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedBorderColor = FlowCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newAlbum = Album(
                                id = "album_${UUID.randomUUID().toString().take(6)}",
                                title = title.trim(),
                                artistId = artists.firstOrNull { it.name == artistName }?.id ?: "artist_1",
                                artistName = artistName.trim(),
                                coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
                                releaseYear = year.toIntOrNull() ?: 2024
                            )
                            onAddAlbum(newAlbum)
                            showAddAlbumDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black)
                ) {
                    Text("Create Album")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAlbumDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Text(title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
