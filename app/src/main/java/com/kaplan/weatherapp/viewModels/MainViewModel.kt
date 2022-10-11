package com.kaplan.weatherapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaplan.weatherapp.repository.MainRepository
import com.kaplan.weatherapp.utils.PlacesSearchEvent
import com.kaplan.weatherapp.utils.PlacesSearchEventError
import com.kaplan.weatherapp.utils.PlacesSearchEventFound
import com.kaplan.weatherapp.utils.PlacesSearchEventLoading
import com.wirefreethought.geodb.client.GeoDbApi
import com.wirefreethought.geodb.client.request.FindPlacesRequest
import com.wirefreethought.geodb.client.request.PlaceRequestType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val geoDbApi: GeoDbApi,
private val repository: MainRepository
): ViewModel() {

    private val _events = MutableLiveData<PlacesSearchEvent>()
    val events: LiveData<PlacesSearchEvent> = _events

    val weatherInfoLiveData get() = repository.weatherInfo

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()

        _events.value = PlacesSearchEventLoading

        val handler = CoroutineExceptionHandler { _, throwable ->
            _events.postValue(PlacesSearchEventError(throwable))
        }
        searchJob = CoroutineScope(Dispatchers.IO).launch(handler) {
            // Add delay so that network call is performed only after there is a 300 ms pause in the
            // search query. This prevents network calls from being invoked if the user is still
            // typing.
            delay(300)
            val placeResponse = geoDbApi.findPlaces(
                FindPlacesRequest.builder()
                    .namePrefix(query)
                    .types(Collections.singleton(PlaceRequestType.CITY))
                    .build()
            )

            _events.postValue(PlacesSearchEventFound(placeResponse.data))
        }
    }

    fun getWeatherDetails(cityName: String){
        viewModelScope.launch {
            repository.getWeatherDetails(cityName)
        }
    }
}