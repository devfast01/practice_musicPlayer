package com.example.practice_musicplayer.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.adapters.AdapterViewPager
import com.example.practice_musicplayer.databinding.FragmentExamplePlayingBinding
import com.example.practice_musicplayer.databinding.FragmentNowPlayingBinding
import com.example.practice_musicplayer.getImageArt
import com.example.practice_musicplayer.setSongPosition
import com.example.practice_musicplayer.utils.OnSwipeTouchListener
import com.example.practice_musicplayer.utils.RetrofitService
import com.example.practice_musicplayer.utils.ViewPagerExtensions.addCarouselEffect
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


@Suppress("DEPRECATION")
class ExamplePlaying : Fragment() {

    lateinit var viewPagerAdapter: AdapterViewPager
    private val apiService = RetrofitService.getInstance()
    private var songList: ArrayList<MusicClass>? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentExamplePlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentExamplePlayingBinding.inflate(inflater, container, false)
        val view: View = binding.root
        binding.root.visibility = View.VISIBLE

        getNewSongs()

        return view
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
//        newSongRecycleData(songList!!)
    } catch (e: JSONException) {
        e.printStackTrace()
    }

//    private fun newSongRecycleData(array: ArrayList<MusicClass>) {
//
//        binding.viewPager.addCarouselEffect()
//        viewPagerAdapter = AdapterViewPager(requireContext(), array)
//        binding.viewPager.adapter = viewPagerAdapter
//
//    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
    }

}