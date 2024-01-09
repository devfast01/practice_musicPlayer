package com.example.practice_musicplayer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.practice_musicplayer.MusicClass
import com.example.practice_musicplayer.R
import com.example.practice_musicplayer.databinding.ItemCarouselBinding
import com.example.practice_musicplayer.databinding.ItemLargeCarouselBinding
import com.example.practice_musicplayer.databinding.SingleLayoutBinding


class AdapterViewPager  (
    private val context: Context,
    private var musicList: ArrayList<MusicClass>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false,
) :
    RecyclerView.Adapter<AdapterViewPager.MyHolder>() {

    class MyHolder(binding: ItemLargeCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        val titleView = binding.mtvItem
        val titleView1 = binding.mtvItem
        val imageView = binding.imageView
        val imageView1 = binding.imageView1
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

        holder.titleView.text = musicList[position].name
        holder.titleView1.text = musicList[position].name

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

        Glide
            .with(context)
            .applyDefaultRequestOptions(myOptions)
            .load(musicList[position].coverArtUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.image_as_cover)
            .into(holder.imageView1)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

}

//
//class AdapterViewPager : ListAdapter<String, AdapterViewPager.CustomViewHolder>(customDiffUtils) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding = DataBindingUtil.inflate<ItemCarouselBinding>(layoutInflater, R.layout.item_carousel, parent, false)
//        return CustomViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        holder.binding.item = getItem(position)
//    }
//
//    inner class CustomViewHolder(val binding: ItemCarouselBinding) : RecyclerView.ViewHolder(binding.root)
//
//    companion object {
//        val customDiffUtils = object : DiffUtil.ItemCallback<String>() {
//            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
//                return oldItem == newItem
//            }
//
//            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}