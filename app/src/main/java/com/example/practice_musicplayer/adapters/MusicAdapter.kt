package com.example.practice_musicplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MainActivity
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.activities.MusicInterface
import com.example.practice_musicplayer.databinding.SingleLayoutBinding
import com.example.practice_musicplayer.formatDuration
import com.google.android.material.snackbar.Snackbar

class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<MusicClass>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false,
) :
    RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    class MyHolder(binding: SingleLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleView = binding.titleView
        val albumName = binding.albumName
        val imageView = binding.imageView
        val duration = binding.duration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            SingleLayoutBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        holder.titleView.text = musicList[position].name
        holder.albumName.text = musicList[position].artist
        holder.duration.text = musicList[position].duration



        val myOptions = RequestOptions()
            .centerCrop()
            .override(100, 100)

        Glide
            .with(context)
            .applyDefaultRequestOptions(myOptions)
            .load(musicList[position].coverArtUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.image_as_cover)
            .into(holder.imageView)

        when {
            playlistDetails -> {
                holder.root.setOnClickListener {
                    Toast.makeText(context, "play list details", Toast.LENGTH_SHORT).show()
                    sendIntent(position = position, parameter = "PlaylistDetailsAdapter")
                }
            }

            else -> {
                holder.itemView.setOnClickListener {
                    if (MainActivity.isSearching) {
                        sendIntent(position = position, parameter = "MusicAdapterSearch")
                    } else {
                        sendIntent(position = position, parameter = "MusicAdapter")
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    fun updateMusicList(searchList: ArrayList<MusicClass>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(position: Int, parameter: String) {
        val intent = Intent(context, MusicInterface::class.java)
        intent.putExtra("index", position)
        intent.putExtra("class", parameter)
        ContextCompat.startActivity(context, intent, null)
    }
}
