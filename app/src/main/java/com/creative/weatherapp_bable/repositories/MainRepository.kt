package com.creative.weatherapp_bable.repositories

import com.creative.weatherapp_bable.apiservice.WeatherApi
import com.creative.weatherapp_bable.model.WeatherDetail
import retrofit2.Response
import javax.inject.Inject


class MainRepository @Inject constructor(private val mainAPICall: WeatherApi) {

    suspend fun getWeatherDetail(country:String, apiKey:String): Response<WeatherDetail> {
        return mainAPICall.getWeatherDetail(country,apiKey)
    }

}