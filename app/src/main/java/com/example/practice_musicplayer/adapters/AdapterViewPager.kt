package com.example.practice_musicplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.databinding.ItemLargeCarouselBinding
import com.google.android.material.card.MaterialCardView

class AdapterViewPager  (
    private val context: Context,
    private var statePanel: Boolean,
    private var musicList: ArrayList<MusicClass>,
    ) :
    RecyclerView.Adapter<AdapterViewPager.MyHolder>() {
    private var selectedPosition: Int = 0

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()  // Notify the adapter that data has changed
    }

//    fun updateSmalCardVisibility(isVisible: Float) {
//        // Iterate through all views and update visibility
//        for (i in 0 until itemCount) {
//            val viewHolder = viewPager.findViewWithTag<MyHolder>(i)
//            viewHolder?.itemView?.findViewById<MaterialCardView>(R.id.smalCard)?.visibility =
//                if (isVisible< 0.5) View.VISIBLE else View.INVISIBLE
//        }
//    }

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

    @SuppressLint("CutPasteId", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
//        val isVisible = (position - 1) == selectedPosition
//        holder.itemView.findViewById<MaterialCardView>(R.id.smalCard).visibility =
//            if (isVisible) View.VISIBLE else View.GONE

//        holder.itemView.findViewById<MaterialCardView>(R.id.smalCard)
//            .tag = "smalCardTag$position"

            holder.itemView.findViewById<MaterialCardView>(R.id.smalCard).visibility =
            if (statePanel) View.VISIBLE else View.GONE

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

//    fun updateSmalCardVisibility(selectedPosition: Int) {
//        // Iterate through all views and update visibility
//        for (i in 0 until itemCount) {
//            val isVisible = i == selectedPosition
//            val viewHolder = findViewHolderForAdapterPosition(i) as? MyHolder
//            viewHolder?.itemView?.findViewById<MaterialCardView>(R.id.smalCard)?.visibility =
//                if (isVisible) View.VISIBLE else View.INVISIBLE
//        }
//    }
}
