package com.kaplan.weatherapp.di

import com.kaplan.weatherapp.BuildConfig.BASE_URL
import com.kaplan.weatherapp.api.WeatherAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun providesWeatherAPI(retrofitBuilder: Retrofit.Builder): WeatherAPI {
        return retrofitBuilder.build().create(WeatherAPI::class.java)
    }
}