package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice_musicplayer.adapters.MusicAdapter
import com.example.practice_musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    lateinit var musicAdapter: MusicAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private val myBroadcastReceiver = MyBroadcastReceiver()

    companion object {
        lateinit var songList: ArrayList<MusicClass>
        lateinit var recyclerView: RecyclerView
        lateinit var musicListSearch: ArrayList<MusicClass>

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMainBinding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Base_Theme_Practice_musicPlayer)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG)
        registerReceiver(myBroadcastReceiver, filter)

        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> {
                    Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                }

                R.id.navAbout -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                }

                R.id.navExit -> {

                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close app?")
                        .setPositiveButton("Yes") { _, _ ->
                            exitApplication()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                }
            }
            true
        }
    }

    private fun init() {


        recyclerView = binding.listView

        musicAdapter = MusicAdapter(this, songList)
        recyclerView.adapter = musicAdapter
        recyclerView.setItemViewCacheSize(50)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)

    }
}



