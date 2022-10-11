package com.kaplan.weatherapp.ui

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import com.kaplan.weatherapp.databinding.ActivityMainBinding
import com.kaplan.weatherapp.utils.*
import com.kaplan.weatherapp.viewModels.MainViewModel
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.wirefreethought.geodb.client.model.PopulatedPlaceSummary
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val boundsIndia = LatLngBounds(LatLng(23.63936, 68.14712), LatLng(28.20453, 97.34466))
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var placesList: MutableList<PopulatedPlaceSummary>
    private val placesViewModel: MainViewModel by viewModels()
    private lateinit var selectedPlace: PopulatedPlaceSummary


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initObservers()
    }

    private fun initUI() {
        setUpMap()
        setUpSearch()
    }

    private fun initObservers() {
        observeMapEvents()
        observeWeatherInfo()
    }

    private fun setUpMap() {
        // get SupportMapFragment and get notified when the map is ready to be used
        val mapFragment =
            supportFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            googleMap = mapFragment.awaitMap()
            googleMap.apply {
                awaitMapLoad()
                animateCamera(CameraUpdateFactory.newLatLngBounds(boundsIndia, 0))
            }
        }
    }

    private fun setUpSearch() {
        binding.searchBar.apply {

            setOnSearchActionListener(object : OnSearchActionListener {
                override fun onButtonClicked(buttonCode: Int) {
                    if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                        // Control navigation drawer
                    } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                        closeSearch()
                    }
                }

                override fun onSearchStateChanged(enabled: Boolean) {}
                override fun onSearchConfirmed(text: CharSequence) {
                    startSearch(text.toString(), true, null, true)
                }
            })

            addTextChangeListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    placesViewModel.onSearchQueryChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable) {}
            })

            setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
                override fun OnItemClickListener(position: Int, v: View?) {
                    if (position >= placesList.size) {
                        return
                    }
                    googleMap.clear()
                    selectedPlace = placesList[position]
                    placesViewModel.getWeatherDetails(selectedPlace.name)
                    val suggestion: String = lastSuggestions[position].toString()
                    text = suggestion

                    Handler().postDelayed({ clearSuggestions() }, 1000)

                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        windowToken,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                    )

                }

                override fun OnItemDeleteListener(position: Int, v: View?) {}

            })
        }
    }

    private fun observeMapEvents() {
        placesViewModel.events.observe(this) { event ->
            when (event) {
                is PlacesSearchEventLoading -> {
                    // progressBar.isIndeterminate = true
                }
                is PlacesSearchEventError -> {
                    // progressBar.isIndeterminate = false
                }
                is PlacesSearchEventFound -> {
                    placesList = event.places.toMutableList()
                    val suggestionsList: MutableList<String> = ArrayList()
                    placesList.forEach {
                        suggestionsList.add(it.name.toString())
                    }

                    binding.searchBar.apply {
                        updateLastSuggestions(suggestionsList)
                        if (isSuggestionsVisible) {
                            showSuggestionsList()
                        }
                    }
                }
            }
        }
    }

    private fun observeWeatherInfo() {
        placesViewModel.weatherInfoLiveData.observe(this) {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    val newPlaceLatLng = LatLng(selectedPlace.latitude, selectedPlace.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPlaceLatLng, 10f))
                    googleMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
                    googleMap.addMarker {
                        position(newPlaceLatLng)
                        title(selectedPlace.name)
                    }.let { mark ->
                        mark?.tag = it.data
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        }
    }
}