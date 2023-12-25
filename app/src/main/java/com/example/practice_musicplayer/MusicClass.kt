@file:Suppress("DEPRECATION")

package com.example.practice_musicplayer

import android.content.ContentValues
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.app.ServiceCompat
import com.example.practice_musicplayer.activities.MusicInterface
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.exitProcess

data class MusicClass(
    val id: Int,
    val date: String,
    var name: String,
    val duration: String,
    var artist: String,
    var coverArtUrl: String,
    var url: String
)

val usedNumber = mutableSetOf<Int>()
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


fun exitApplicationNotification() {
//    if (MusicInterface.isPlaying) {
//        val musicInterface = MusicInterface()
//        musicInterface.pauseMusic()
//    }
    Log.e("serStop", MusicInterface.musicService.toString())
    MusicInterface.musicService!!.stopForeground(true)
//    MusicInterface.myService!!.stopForeground(true)

}
fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(
        duration, TimeUnit.MILLISECONDS
    ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}
fun shuffleSongs() {
    var newSong: Int = MusicInterface.songPosition
    checkIfListIsFull(usedNumber)
    Log.d(ContentValues.TAG, "shuffleSongs: " + usedNumber.size)
    while (newSong == MusicInterface.songPosition) {
        newSong = getRandomNumber(MusicInterface.musicList.size)
    }
    MusicInterface.songPosition = newSong
}

fun getRandomNumber(max: Int): Int {
    val random = Random
    var number = random.nextInt(max + 1)

    while (usedNumber.contains(number)) {
        number = random.nextInt(max + 1)
    }

    usedNumber.add(number)
    return number
}
fun checkIfListIsFull(list: MutableSet<Int>) {
    if (list.size.toInt() == MusicInterface.musicList.size) {
        list.clear()
    }
}

fun setSongPosition(increment: Boolean) {
    if (!MusicInterface.isRepeating) {
        if (increment) {
            if (MusicInterface.isShuffling && MusicInterface.counter == 0) {
                shuffleSongs()
            } else {
                if (MusicInterface.musicList.size - 1 == MusicInterface.songPosition) {
                    MusicInterface.songPosition = 0
                } else ++MusicInterface.songPosition
            }
        } else {
            if (0 == MusicInterface.songPosition) MusicInterface.songPosition =
                MusicInterface.musicList.size - 1
            else --MusicInterface.songPosition
        }
    }
}