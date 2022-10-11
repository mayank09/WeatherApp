package com.kaplan.weatherapp.utils

import com.wirefreethought.geodb.client.model.PopulatedPlaceSummary

sealed class PlacesSearchEvent

object PlacesSearchEventLoading : PlacesSearchEvent()

data class PlacesSearchEventError(
    val exception: Throwable
) : PlacesSearchEvent()

data class PlacesSearchEventFound(
    val places: List<PopulatedPlaceSummary>
) : PlacesSearchEvent()
