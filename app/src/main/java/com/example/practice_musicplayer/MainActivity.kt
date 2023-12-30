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
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.adapters.MusicAdapter
import com.example.practice_musicplayer.databinding.ActivityMainBinding
import com.example.practice_musicplayer.utils.RetrofitService
import com.example.practice_musicplayer.utils.WaveAnimation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.simform.refresh.SSPullToRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    lateinit var musicAdapter: MusicAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private val myBroadcastReceiver = MyBroadcastReceiver()
    private val apiService = RetrofitService.getInstance()

    companion object {
        var songList: ArrayList<MusicClass>? = null
        lateinit var recyclerView: RecyclerView
        lateinit var musicListSearch: ArrayList<MusicClass>
        var isSearching: Boolean = false

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

        getNewSongs()

        //for refreshing layout on swipe from top
        binding.refreshLayout.setRefreshView(WaveAnimation(this@MainActivity))
        binding.refreshLayout.setOnRefreshListener(object :
            SSPullToRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    getNewSongs()
                    Log.d("Begli" , songList.toString())
                    binding.refreshLayout.setRefreshing(false) // This stops refreshing
                }
            }
        })

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

    fun getNewSongs() {
        val apiService =
            apiService.getNewSongs(
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

    private fun recycleNewSongs(response: String) = try {
        val modelRecyclerArrayList: ArrayList<MusicClass> = ArrayList()
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

        songList = modelRecyclerArrayList
        newSongRecycleData(songList!!)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

    private fun newSongRecycleData(array: ArrayList<MusicClass>) {
        recyclerView = binding.listView
        musicAdapter = MusicAdapter(this, array)
        recyclerView.adapter = musicAdapter
        recyclerView.setItemViewCacheSize(50)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBroadcastReceiver)
        exitApplication()
    }

    override fun onResume() {
        super.onResume()
        if (MusicInterface.musicService != null) binding.nowPlaying.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)

    }

}



