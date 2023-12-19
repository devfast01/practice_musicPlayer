package com.example.practice_musicplayer

import android.media.MediaMetadataRetriever
import com.example.practice_musicplayer.activities.FavouriteActivity
import com.example.practice_musicplayer.activities.MusicInterface
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class MusicClass(
    val id: String,
    val title: String,
    val album: String,
    val length: Long = 0,
    val artist: String,
    val path: String,
    val artUri: String,
)

class Playlist {
    lateinit var name: String
    lateinit var playlist: ArrayList<MusicClass>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class MusicPlaylist {
    var ref: ArrayList<Playlist> = ArrayList()
}

fun exitApplication() {

    exitProcess(1)
}

fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun favouriteCheck(id: String): Int {
    MusicInterface.isLiked = false
    FavouriteActivity.favSongList.forEachIndexed { index, music ->
        if (id == music.id) {
            MusicInterface.isLiked = true
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<MusicClass>): ArrayList<MusicClass> {
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if (!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}

fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(
        duration, TimeUnit.MILLISECONDS
    ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}