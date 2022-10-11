package com.kaplan.weatherapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.kaplan.weatherapp.R
import com.kaplan.weatherapp.databinding.InfoWindowWeatherBinding
import com.kaplan.weatherapp.di.GlideApp
import com.kaplan.weatherapp.model.WeatherResponse


class CustomInfoWindowForGoogleMap(private val mContext: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View? {
        val binding: InfoWindowWeatherBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.info_window_weather,
            null,
            true
        )
        return binding.let {
            it.weatherInfo = marker.tag as WeatherResponse
            it.executePendingBindings()
            binding.root
        }
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    //alternate approach if binding adapter doesn't works
    private fun loadImage(icon: String, view: AppCompatImageView, marker: Marker) {
        GlideApp.with(mContext).load(ImageBaseURL.plus(icon).plus(".png"))
            .apply(
                RequestOptions.circleCropTransform().placeholder(R.drawable.ic_baseline_search_24)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("## FIRST RESOURCE-", "-".plus(isFirstResource))
                    Log.d("## FIRST DATA SOURCE-", "-".plus(dataSource?.name))
                    view.setImageDrawable(resource)
                    if (dataSource?.name.equals("DATA_DISK_CACHE")) {
                        marker.showInfoWindow()
                    }
                    return true
                }
            }).into(view)
    }
}