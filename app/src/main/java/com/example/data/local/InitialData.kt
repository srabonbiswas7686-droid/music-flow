package com.example.data.local

object InitialData {

    val artists = listOf(
        ArtistEntity(
            id = "artist_1",
            name = "Astral Soundscape",
            bio = "Pioneering cinematic electronic journeys, lush synthesizers, and energetic futuristic basslines.",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            monthlyListeners = 1250000,
            isFollowed = true
        ),
        ArtistEntity(
            id = "artist_2",
            name = "Luna Mirage",
            bio = "Ethereal dream-pop with silky vocals, melancholic guitars, and sparkling reverb.",
            imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            monthlyListeners = 890000,
            isFollowed = true
        ),
        ArtistEntity(
            id = "artist_3",
            name = "Neo Rhythm Collective",
            bio = "Modern neo-soul fusion featuring funky basslines, Rhodes keyboards, and tight drum grooves.",
            imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            monthlyListeners = 640000,
            isFollowed = false
        ),
        ArtistEntity(
            id = "artist_4",
            name = "Midnight Chillout",
            bio = "Warm analog tape loops, cozy vinyl crackles, and relaxing downtempo hip-hop beats.",
            imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            monthlyListeners = 2100000,
            isFollowed = true
        ),
        ArtistEntity(
            id = "artist_5",
            name = "Velvet Horizon",
            bio = "Atmospheric indie acoustic duo with rich vocal harmonies and storytelling lyricism.",
            imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            monthlyListeners = 480000,
            isFollowed = false
        )
    )

    val albums = listOf(
        AlbumEntity(
            id = "album_1",
            title = "Echoes in the Neon",
            artistId = "artist_1",
            artistName = "Astral Soundscape",
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            releaseYear = 2024,
            isSaved = true
        ),
        AlbumEntity(
            id = "album_2",
            title = "Starlight Reverie",
            artistId = "artist_2",
            artistName = "Luna Mirage",
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            releaseYear = 2023,
            isSaved = true
        ),
        AlbumEntity(
            id = "album_3",
            title = "Urban Velvet",
            artistId = "artist_3",
            artistName = "Neo Rhythm Collective",
            coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            releaseYear = 2024,
            isSaved = false
        ),
        AlbumEntity(
            id = "album_4",
            title = "Coffee & Rainy Windows",
            artistId = "artist_4",
            artistName = "Midnight Chillout",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            releaseYear = 2023,
            isSaved = true
        ),
        AlbumEntity(
            id = "album_5",
            title = "Golden Hour Echoes",
            artistId = "artist_5",
            artistName = "Velvet Horizon",
            coverUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80",
            releaseYear = 2024,
            isSaved = false
        )
    )

    val songs = listOf(
        SongEntity(
            id = "song_1",
            title = "Neon Skyline",
            artistId = "artist_1",
            artistName = "Astral Soundscape",
            albumId = "album_1",
            albumTitle = "Echoes in the Neon",
            durationMs = 372000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            playCount = 1420
        ),
        SongEntity(
            id = "song_2",
            title = "Starlight Mirage",
            artistId = "artist_2",
            artistName = "Luna Mirage",
            albumId = "album_2",
            albumTitle = "Starlight Reverie",
            durationMs = 423000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
            genre = "Indie Pop",
            playCount = 1180
        ),
        SongEntity(
            id = "song_3",
            title = "Velvet Midnight",
            artistId = "artist_3",
            artistName = "Neo Rhythm Collective",
            albumId = "album_3",
            albumTitle = "Urban Velvet",
            durationMs = 344000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            genre = "R&B / Soul",
            playCount = 890
        ),
        SongEntity(
            id = "song_4",
            title = "Rainy Café Beats",
            artistId = "artist_4",
            artistName = "Midnight Chillout",
            albumId = "album_4",
            albumTitle = "Coffee & Rainy Windows",
            durationMs = 302000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            genre = "Lo-Fi",
            playCount = 2300
        ),
        SongEntity(
            id = "song_5",
            title = "Solar Odyssey",
            artistId = "artist_1",
            artistName = "Astral Soundscape",
            albumId = "album_1",
            albumTitle = "Echoes in the Neon",
            durationMs = 350000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            playCount = 760
        ),
        SongEntity(
            id = "song_6",
            title = "Drifting Thoughts",
            artistId = "artist_2",
            artistName = "Luna Mirage",
            albumId = "album_2",
            albumTitle = "Starlight Reverie",
            durationMs = 380000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            coverUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            genre = "Indie Pop",
            playCount = 650
        ),
        SongEntity(
            id = "song_7",
            title = "Sunset Drive",
            artistId = "artist_5",
            artistName = "Velvet Horizon",
            albumId = "album_5",
            albumTitle = "Golden Hour Echoes",
            durationMs = 310000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            coverUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80",
            genre = "Acoustic",
            playCount = 940
        ),
        SongEntity(
            id = "song_8",
            title = "Coffee Shop Afternoon",
            artistId = "artist_4",
            artistName = "Midnight Chillout",
            albumId = "album_4",
            albumTitle = "Coffee & Rainy Windows",
            durationMs = 315000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            genre = "Lo-Fi",
            playCount = 1840
        ),
        SongEntity(
            id = "song_9",
            title = "Cybernetic Pulse",
            artistId = "artist_1",
            artistName = "Astral Soundscape",
            albumId = "album_1",
            albumTitle = "Echoes in the Neon",
            durationMs = 335000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            genre = "Electronic",
            playCount = 1120
        ),
        SongEntity(
            id = "song_10",
            title = "Golden Slumber",
            artistId = "artist_5",
            artistName = "Velvet Horizon",
            albumId = "album_5",
            albumTitle = "Golden Hour Echoes",
            durationMs = 290000,
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            genre = "Acoustic",
            playCount = 520
        )
    )

    val playlists = listOf(
        PlaylistEntity(
            id = "playlist_1",
            title = "Today's Top Flow",
            description = "The freshest tracks and hottest rhythms currently trending across Music Flow.",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            isCustom = false
        ),
        PlaylistEntity(
            id = "playlist_2",
            title = "Lo-Fi Focus & Study",
            description = "Mellow analog beats, relaxing vinyl texture, and smooth groove for work or study.",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            isCustom = false
        ),
        PlaylistEntity(
            id = "playlist_3",
            title = "Synthwave Night Drive",
            description = "Futuristic 80s synthesizers, retro arpeggios, and midnight neon energy.",
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
            isCustom = false
        ),
        PlaylistEntity(
            id = "playlist_4",
            title = "Acoustic Sunset Chill",
            description = "Warm acoustic guitars, indie folk melodies, and golden twilight harmonies.",
            coverUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=600&auto=format&fit=crop&q=80",
            isCustom = true
        )
    )

    val playlistSongs = listOf(
        PlaylistSongCrossRef("playlist_1", "song_1", 0),
        PlaylistSongCrossRef("playlist_1", "song_2", 1),
        PlaylistSongCrossRef("playlist_1", "song_4", 2),
        PlaylistSongCrossRef("playlist_1", "song_3", 3),

        PlaylistSongCrossRef("playlist_2", "song_4", 0),
        PlaylistSongCrossRef("playlist_2", "song_8", 1),
        PlaylistSongCrossRef("playlist_2", "song_2", 2),

        PlaylistSongCrossRef("playlist_3", "song_1", 0),
        PlaylistSongCrossRef("playlist_3", "song_5", 1),
        PlaylistSongCrossRef("playlist_3", "song_9", 2),

        PlaylistSongCrossRef("playlist_4", "song_7", 0),
        PlaylistSongCrossRef("playlist_4", "song_10", 1),
        PlaylistSongCrossRef("playlist_4", "song_6", 2)
    )

    val favorites = listOf(
        FavoriteEntity("song_1"),
        FavoriteEntity("song_4"),
        FavoriteEntity("song_2")
    )

    val users = listOf(
        UserEntity(
            id = "user_demo",
            username = "alex_music",
            displayName = "Alex Rivera",
            email = "alex@musicflow.app",
            isAdmin = true,
            isActive = true
        ),
        UserEntity(
            id = "user_guest",
            username = "guest_listener",
            displayName = "Guest Explorer",
            email = "guest@musicflow.app",
            isAdmin = false,
            isActive = false
        )
    )
}
