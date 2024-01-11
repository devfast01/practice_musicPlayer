@file:Suppress("DEPRECATION")

package com.example.practice_musicplayer

import android.content.ContentValues
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.app.ServiceCompat
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.utils.RetrofitService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
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

fun getMainColor(img: Bitmap): Int {
    val newImg = Bitmap.createScaledBitmap(img, 1, 1, true)
    val color = newImg.getPixel(0, 0)
    newImg.recycle()
    return manipulateColor(color, 0.4.toFloat())
}
fun manipulateColor(color: Int, factor: Float): Int {
    val a: Int = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(
        a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255)
    )
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

fun getNewSongs() {
    val api_Service = RetrofitService.getInstance()

    val apiService =
        api_Service.getNewSongs(
        )

    apiService?.enqueue(object : Callback<String?> {
        override fun onResponse(call: Call<String?>, response: Response<String?>) {
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val jsonresponse: String = response.body().toString()
                    // on below line we are initializing our adapter.
//                        Log.d("response", jsonresponse.toString())
                    recycleNewSongs(jsonresponse)
                } else {
                    Log.i(
                        "onEmptyResponse",
                        "Returned empty response"
                    )
                }
            }
        }

        override fun onFailure(call: Call<String?>, t: Throwable) {
            Log.e(
                "ERROR",
                t.message.toString()
            )
        }
    })
}

fun recycleNewSongs(response: String) = try {
    val modelRecyclerArrayList: java.util.ArrayList<MusicClass> = java.util.ArrayList()
    val obj = JSONObject(response)
    val dataArray = obj.getJSONArray("data")
//        Log.d("RESPONSE", dataArray.toString())

    for (i in 0 until dataArray.length()) {
        val dataObject = dataArray.getJSONObject(i)

        val musicItem = MusicClass(
            id = dataObject.getInt("id"),
            date = dataObject.getString("date"),
            name = dataObject.getString("name"),
            duration = dataObject.getString("duration"),
            artist = dataObject.getString("artist"),
            coverArtUrl = dataObject.getString("cover_art_url"),
            url = dataObject.getString("url")
        )

        modelRecyclerArrayList.add(musicItem)
    }

//        Log.d("RESPONSE", modelRecyclerArrayList.toString())

    MainActivity.songList = modelRecyclerArrayList

} catch (e: JSONException) {
    e.printStackTrace()
}
