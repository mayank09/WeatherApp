package com.kaplan.weatherapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kaplan.weatherapp.api.WeatherAPI
import com.kaplan.weatherapp.model.WeatherResponse
import com.kaplan.weatherapp.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class MainRepository @Inject constructor(private val api: WeatherAPI) {

    private val _weatherInfo = MutableLiveData<NetworkResult<WeatherResponse>>()
    val weatherInfo : LiveData<NetworkResult<WeatherResponse>> = _weatherInfo

    suspend fun getWeatherDetails(cityName: String){
        _weatherInfo.postValue(NetworkResult.Loading())
        val response = api.getWeather(cityName)
        if(response.isSuccessful && response.body() != null){
            _weatherInfo.postValue(NetworkResult.Success(response.body()!!))
        }else if(response.errorBody() != null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _weatherInfo.postValue(NetworkResult.Error(errorObj.getString("message")))
        }else{
            _weatherInfo.postValue(NetworkResult.Error("Something went wrong"))
        }
    }

}