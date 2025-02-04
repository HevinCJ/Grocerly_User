package com.example.grocerly.adapters

import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil3.load
import coil3.request.crossfade
import com.bumptech.glide.Glide
import java.io.File

object OfferBindingAdapter {

    @BindingAdapter("android:setOfferImage")
    @JvmStatic
    fun setOfferImage(imageView: ImageView,savedFile: String){

        try {
            Glide.with(imageView.context)
                .load(savedFile)
                .into(imageView)

        }catch (e:Exception){

        }

    }


    @BindingAdapter("android:setCategoryImage")
    @JvmStatic
    fun setCategoryImage(imageView: ImageView,url: Int){
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }

        @BindingAdapter("android:setFormattedRating")
        @JvmStatic
        fun setFormattedRating(view:TextView,value:Double) {
            view.text = String.format("%.1f", value)
            Log.d("formattedrating",value.toString())
        }


    @JvmStatic
    @BindingAdapter("app:formattedTotalRating")
    fun setFormattedTotalRating(view: TextView, totalRating: Int) {
       view.text = "($totalRating)"
    }


}