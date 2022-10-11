package com.kaplan.weatherapp.api

import com.kaplan.weatherapp.BuildConfig
import com.kaplan.weatherapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//end point
private const val WEATHER = "weather"

//parameters
private const val PARAM_CITY = "q"
private const val PARAM_API_KEY = "appid"
private const val PARAM_UNITS = "units"

//arguments
private const val UNITS_METRIC = "metric"

interface WeatherAPI {

    @GET(WEATHER)
    suspend fun getWeather(
        @Query(PARAM_CITY) cityName: String,
        @Query(PARAM_API_KEY) apikey: String = BuildConfig.OPEN_WEATHER_API_KEY,
        @Query(PARAM_UNITS) units: String = UNITS_METRIC
    ): Response<WeatherResponse>
}