package com.example.practice_musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.request.transition.Transition.ViewAdapter
import com.example.practice_musicplayer.adapters.AdapterViewPager
import com.example.practice_musicplayer.adapters.MusicAdapter
import com.example.practice_musicplayer.databinding.ActivityViewPagerBinding
import com.example.practice_musicplayer.utils.DpData
import com.example.practice_musicplayer.utils.RetrofitService
import com.example.practice_musicplayer.utils.ViewPagerExtensions.addCarouselEffect
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ViewPager : AppCompatActivity() {
    private val binding by lazy { ActivityViewPagerBinding.inflate(layoutInflater) }
    lateinit var viewPagerAdapter: AdapterViewPager
    private val apiService = RetrofitService.getInstance()
    private var songList: ArrayList<MusicClass>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getNewSongs()

    }

    private fun initViewPager() {
//        binding.viewPager.addCarouselEffect()
//        binding.viewPager.adapter = adapter
//        adapter.submitList(dpData.dummyData)
    }

    private fun getNewSongs() {
        val apiService =
            apiService.getNewSongs(
            )

        apiService?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val jsonresponse: String = response.body().toString()
                        // on below line we are initializing our adapter.
                        Log.d("response", jsonresponse)
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

        binding.viewPager.addCarouselEffect()
        viewPagerAdapter = AdapterViewPager(this, array)
        binding.viewPager.adapter = viewPagerAdapter

    }

}