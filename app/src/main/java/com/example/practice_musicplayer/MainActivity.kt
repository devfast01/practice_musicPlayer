package com.example.practice_musicplayer

import android.content.Intent
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.RecyclerView
import com.example.practice_musicplayer.activities.AboutActivity
import com.example.practice_musicplayer.activities.FavouriteActivity
import com.example.practice_musicplayer.activities.FeedbackActivity
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    companion object {
        var isSearching: Boolean = false
        lateinit var songList: ArrayList<MusicClass>
        lateinit var recyclerView: RecyclerView
        lateinit var musicListSearch: ArrayList<MusicClass>
        var playNextList: ArrayList<MusicClass> = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Base_Theme_Practice_musicPlayer)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> {
                    startActivity(Intent(this, FeedbackActivity::class.java))
                }

                R.id.navAbout -> {
                    startActivity(Intent(this, AboutActivity::class.java))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)

    }
}



