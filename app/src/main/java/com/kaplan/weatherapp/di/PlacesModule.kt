package com.kaplan.weatherapp.di

import com.wirefreethought.geodb.client.GeoDbApi
import com.wirefreethought.geodb.client.model.GeoDbInstanceType
import com.wirefreethought.geodb.client.net.GeoDbApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object PlacesModule {

    @Singleton
    @Provides
    fun provideGeoDBClient(): GeoDbApiClient = GeoDbApiClient(GeoDbInstanceType.FREE)

    @Singleton
    @Provides
    fun providesGeoDBApi(geoDbApiClient: GeoDbApiClient): GeoDbApi = GeoDbApi(geoDbApiClient)
}