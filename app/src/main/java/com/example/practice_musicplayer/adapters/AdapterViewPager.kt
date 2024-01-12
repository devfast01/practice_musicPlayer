package com.example.practice_musicplayer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.databinding.ItemCarouselBinding
import com.example.practice_musicplayer.databinding.ItemLargeCarouselBinding
import com.example.practice_musicplayer.databinding.SingleLayoutBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso


class AdapterViewPager  (
    private val context: Context,
    private var musicList: ArrayList<MusicClass>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false,

) :
    RecyclerView.Adapter<AdapterViewPager.MyHolder>() {

    class MyHolder(binding: ItemLargeCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        val songName_Up = binding.mtvItem
        val songName = binding.songName
        val singerName = binding.singerName
        val imgSmall = binding.imgSmall
        val imgLarge = binding.imgLarge
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemLargeCarouselBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
//
        holder.songName_Up.text = musicList[position].name
        holder.songName.text = musicList[position].name
        holder.singerName.text = musicList[position].artist

        val optionSmall = RequestOptions()
            .centerCrop()
            .override(100, 100)
        Glide
            .with(context)
            .applyDefaultRequestOptions(optionSmall)
            .load(musicList[position].coverArtUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.image_as_cover)
            .into(holder.imgSmall)

        val otionsLarge = RequestOptions()
            .format(DecodeFormat.PREFER_ARGB_8888) // Use higher quality ARGB_8888 format
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and transformed images
            .override(500, 500) // Load the original image size

        Glide
            .with(context)
            .load(musicList[position].coverArtUrl)
            .apply(otionsLarge)
            .error(R.drawable.image_as_cover)
            .into(holder.imgLarge)

    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}
