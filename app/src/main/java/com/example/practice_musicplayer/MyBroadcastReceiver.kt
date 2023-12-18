package com.example.practice_musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.practice_musicplayer.utils.ApplicationClass

class MyBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
//            ApplicationClass.PREVIOUS -> {
//                prevNextMusic(increment = false, context = context!!)
//            }
        }
    }
}