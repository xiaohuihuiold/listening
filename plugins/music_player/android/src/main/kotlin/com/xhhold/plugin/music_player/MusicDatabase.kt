package com.xhhold.plugin.music_player

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xhhold.plugin.music_player.dao.*
import com.xhhold.plugin.music_player.entity.*

@Database(
    entities = [
        Album::class,
        Artist::class,
        Music::class,
        Playlist::class,
        PlaylistMap::class,
        NowPlaylist::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: MusicDatabase? = null

        fun getInstance(context: Context): MusicDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MusicDatabase {
            return Room.databaseBuilder(
                context,
                MusicDatabase::class.java,
                "music.db"
            ).apply {
                allowMainThreadQueries()
            }.build()
        }
    }

    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun musicDao(): MusicDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun nowPlaylistDao(): NowPlaylistDao
}