package com.example.grocerly.adapters

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.util.Locale
import androidx.core.graphics.toColorInt

object OfferBindingAdapter {

    @BindingAdapter("setOfferImage")
    @JvmStatic
    fun setOfferImage(imageView: ImageView,savedFile: String){

        try {
            Glide.with(imageView.context)
                .load(savedFile)
                .into(imageView)

        }catch (e:Exception){
            e.printStackTrace()
        }

    }


    @BindingAdapter("setCategoryImage")
    @JvmStatic
    fun setCategoryImage(imageView: ImageView,url: Int){
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("setFormattedRating")
    fun setFormattedRating(view: TextView, value: Double) {
        view.text = String.format(Locale.getDefault(), "%.1f", value)
    }


    @JvmStatic
    @BindingAdapter("formattedTotalRating")
    fun setFormattedTotalRating(view: TextView, totalRating: Int) {
       view.text = buildString {
        append("(")
        append(totalRating)
        append(")")
    }
    }

    @JvmStatic
    @BindingAdapter("buttonTextColor")
    fun setButtonTextColor(button: Button, colorString: String?) {
        if (!colorString.isNullOrEmpty()) {
            try {
                button.setTextColor(colorString.toColorInt())
            } catch (e: IllegalArgumentException) {
                button.setTextColor(Color.BLACK)
                e.printStackTrace()
            }
        }
    }


    @BindingAdapter("android:setOfferBackgroundColor")
    @JvmStatic
    fun setOfferBackgroundColor(view: ConstraintLayout, colorString: Int) {
        colorString.let {
            view.setBackgroundColor(it)
        }
    }


}