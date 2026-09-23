package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

enum class SearchCategory {
    ALL, SONGS, ARTISTS, ALBUMS, PLAYLISTS
}

data class GenreCardData(val name: String, val gradient: List<Color>)

@Composable
fun SearchScreen(
    songs: List<Song>,
    artists: List<Artist>,
    albums: List<Album>,
    playlists: List<Playlist>,
    playbackState: PlaybackState,
    onSongPlay: (Song, List<Song>) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SearchCategory.ALL) }

    val genres = remember {
        listOf(
            GenreCardData("Electronic", listOf(Color(0xFF7B2CBF), Color(0xFF00E5FF))),
            GenreCardData("Lo-Fi Chill", listOf(Color(0xFF9D0208), Color(0xFFE85D04))),
            GenreCardData("Indie Pop", listOf(Color(0xFF0077B6), Color(0xFF90E0EF))),
            GenreCardData("R&B / Soul", listOf(Color(0xFF5A189A), Color(0xFFFF007F))),
            GenreCardData("Acoustic", listOf(Color(0xFF2B9348), Color(0xFF80B918))),
            GenreCardData("Synthwave", listOf(Color(0xFFF72585), Color(0xFF7209B7))),
            GenreCardData("Ambient", listOf(Color(0xFF1D3557), Color(0xFF457B9D))),
            GenreCardData("Focus Beats", listOf(Color(0xFF3A0CA3), Color(0xFF4CC9F0)))
        )
    }

    val filteredSongs = remember(searchQuery, songs) {
        if (searchQuery.isBlank()) emptyList()
        else songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artistName.contains(searchQuery, ignoreCase = true) ||
            it.albumTitle.contains(searchQuery, ignoreCase = true) ||
            it.genre.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredArtists = remember(searchQuery, artists) {
        if (searchQuery.isBlank()) emptyList()
        else artists.filter { it.name.contains(searchQuery, ignoreCase = true) || it.bio.contains(searchQuery, ignoreCase = true) }
    }

    val filteredAlbums = remember(searchQuery, albums) {
        if (searchQuery.isBlank()) emptyList()
        else albums.filter { it.title.contains(searchQuery, ignoreCase = true) || it.artistName.contains(searchQuery, ignoreCase = true) }
    }

    val filteredPlaylists = remember(searchQuery, playlists) {
        if (searchQuery.isBlank()) emptyList()
        else playlists.filter { it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(top = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search songs, artists, albums...", color = TextTertiary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = FlowCyan)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedBorderColor = FlowCyan,
                unfocusedBorderColor = DarkCardBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("search_text_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SearchCategory.values()) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            category.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (selectedCategory == category) Color.Black else TextPrimary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FlowCyan,
                        containerColor = DarkSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == category,
                        borderColor = DarkCardBorder,
                        selectedBorderColor = FlowCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (searchQuery.isBlank()) {
            // Browse Genres Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Explore Genres & Moods",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(genres) { genre ->
                        Box(
                            modifier = Modifier
                                .height(95.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(genre.gradient))
                                .clickable { searchQuery = genre.name }
                                .padding(14.dp)
                        ) {
                            Text(
                                text = genre.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            // Filtered results list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 120.dp)
            ) {
                // Artists matches
                if (selectedCategory == SearchCategory.ALL || selectedCategory == SearchCategory.ARTISTS) {
                    if (filteredArtists.isNotEmpty()) {
                        item {
                            Text(
                                "Artists",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlowVioletLight,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(filteredArtists) { artist ->
                                    ArtistCard(artist = artist, onClick = { onArtistClick(artist.id) })
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // Songs matches
                if (selectedCategory == SearchCategory.ALL || selectedCategory == SearchCategory.SONGS) {
                    if (filteredSongs.isNotEmpty()) {
                        item {
                            Text(
                                "Songs (${filteredSongs.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlowCyan,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(filteredSongs) { song ->
                            SongRowItem(
                                song = song,
                                isPlaying = playbackState.isPlaying,
                                isCurrentSong = playbackState.currentSong?.id == song.id,
                                onPlayClick = { onSongPlay(song, filteredSongs) },
                                onFavoriteClick = { onFavoriteToggle(song.id) },
                                onAddToPlaylistClick = { onAddToPlaylist(song) },
                                onAddToQueueClick = { onAddToQueue(song) }
                            )
                        }
                    }
                }

                // Albums matches
                if (selectedCategory == SearchCategory.ALL || selectedCategory == SearchCategory.ALBUMS) {
                    if (filteredAlbums.isNotEmpty()) {
                        item {
                            Text(
                                "Albums",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlowPink,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(filteredAlbums) { album ->
                                    AlbumCard(album = album, onClick = { onAlbumClick(album.id) })
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // Playlists matches
                if (selectedCategory == SearchCategory.ALL || selectedCategory == SearchCategory.PLAYLISTS) {
                    if (filteredPlaylists.isNotEmpty()) {
                        item {
                            Text(
                                "Playlists",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlowVioletLight,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(filteredPlaylists) { playlist ->
                                    PlaylistCard(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
                                }
                            }
                        }
                    }
                }

                if (filteredSongs.isEmpty() && filteredArtists.isEmpty() && filteredAlbums.isEmpty() && filteredPlaylists.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.MusicOff,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No results found for \"$searchQuery\"", color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
