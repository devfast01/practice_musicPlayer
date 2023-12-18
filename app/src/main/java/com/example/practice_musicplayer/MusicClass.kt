package com.example.practice_musicplayer

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

fun exitApplication() {

    exitProcess(1)
}
