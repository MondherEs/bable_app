package com.creative.weatherapp_bable.apiservice

import com.creative.weatherapp_bable.model.WeatherDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?")
    suspend fun getWeatherDetail(@Query("q") number: String,@Query("appid") appID:String): Response<WeatherDetail>

}