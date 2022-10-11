package com.kaplan.weatherapp.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kaplan.weatherapp.R
import com.kaplan.weatherapp.di.GlideApp


const val ImageBaseURL = "http://openweathermap.org/img/w/"

@BindingAdapter("load")
fun ImageView.loadImage(url: String) {
    url.let {
        GlideApp.with(this)
            .asBitmap()
            .load(ImageBaseURL.plus(it).plus(".png"))
            .placeholder(R.drawable.ic_baseline_waves_24)
            .error(R.drawable.ic_baseline_search_24)
            .dontAnimate()
            .into(this)
    }
}

@BindingAdapter("temperature")
fun TextView.showTemp(double: Double) {
    this.text = "Temp: ".plus(double.toString().plus(" ")).plus("\u2103")
}

@BindingAdapter("wind")
fun TextView.showWind(double: Double) {
    this.text = "Wind Speed : ".plus(double.toString()).plus(" kph")
}

@BindingAdapter("humidity")
fun TextView.showHumidity(int: Int) {
    this.text = "Humidity : ".plus(int.toString()).plus("%")
}