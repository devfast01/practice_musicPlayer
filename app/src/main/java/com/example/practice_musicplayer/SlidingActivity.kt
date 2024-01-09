package com.example.practice_musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.adapters.AdapterViewPager
import com.example.practice_musicplayer.databinding.ActivitySlidingBinding
import com.example.practice_musicplayer.fragments.ExamplePlaying
import com.example.practice_musicplayer.utils.RetrofitService
import com.example.practice_musicplayer.utils.ViewPagerExtensions.addCarouselEffect
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


open class SlidingActivity : AppCompatActivity(){
    private lateinit var binding:ActivitySlidingBinding
    lateinit var viewPagerAdapter: AdapterViewPager
    private val apiService = RetrofitService.getInstance()
    private var songList: ArrayList<MusicClass>? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlidingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val slidingLayout: SlidingUpPanelLayout = findViewById(R.id.sliding_layout)
//        val text: TextView = findViewById(R.id.text)
//        val btnShow: Button = findViewById(R.id.btn_show)
//        val btnHide: Button = findViewById(R.id.btn_hide)

        // Convert DP to pixels
        val pixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            65f,
            resources.displayMetrics
        ).toInt()

        binding.btnSlide.setOnClickListener {
            // Slide up the panel when the button is clicked
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.btnVisible.setOnClickListener {
            // Slide up the panel when the button is clicked
            slidingLayout.panelHeight = pixels
        }


        binding.btnGone.setOnClickListener {
            // Slide up the panel when the button is clicked
            slidingLayout.panelHeight = 0
        }

        // Set up the Sliding UpPanelLayout
        slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                // Do something when the panel is sliding
                Log.e("panel", "Sliding")
            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                // Do something when the panel state changes
                Log.e("panel", "Hide")
            }
        })


        getNewSongs()
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

    override fun onResume() {
        super.onResume()
        binding.nowPlaying123.visibility = View.VISIBLE
    }
}