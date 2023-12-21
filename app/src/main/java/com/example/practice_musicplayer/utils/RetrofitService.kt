package com.example.practice_musicplayer.utils

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface RetrofitService {


//    @GET("api/v1/song?sort=date&order=desc&max=10")
//    fun getMainPage(): Call<SongResponse?>?

    @GET("api/v1/song?sort=date&order=desc&max=10")
    fun getNewSongs(): Call<String?>?

    @GET("api/v1/song?sort=date&order=desc&max=10&artistId=47%2C48%2C106%2C102%2C17%2C44%2C37%2C70%2C8%2C49%2C57%2C4%2C35%2C38%2C46%2C53%2C16%2C51%2C55%2C52%2C61%2C24%2C15%2C127%2C460%2C790%2C104")
    fun getTrendSongs(): Call<String?>?

    @GET("api/v1/video?sort=date&order=desc&videoTypeId=1")
    fun getClips(): Call<String?>?


    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://aydym.com/")
                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}