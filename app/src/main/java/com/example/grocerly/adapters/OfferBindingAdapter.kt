package com.example.grocerly.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object OfferBindingAdapter {

    @BindingAdapter("android:setOfferImage")
    @JvmStatic
    fun setOfferImage(imageView: ImageView,url: String){
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }


    @BindingAdapter("android:setCategoryImage")
    @JvmStatic
    fun setCategoryImage(imageView: ImageView,url: Int){
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }


}