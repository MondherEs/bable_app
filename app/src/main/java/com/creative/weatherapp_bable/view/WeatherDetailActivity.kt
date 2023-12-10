package com.creative.weatherapp_bable.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.creative.weatherapp_bable.MainActivity
import com.creative.weatherapp_bable.R
import com.creative.weatherapp_bable.databinding.ActivityMainBinding
import com.creative.weatherapp_bable.databinding.ActivityWeatherDetailBinding
import com.creative.weatherapp_bable.model.WeatherDetail
import com.creative.weatherapp_bable.utils.Constants
import com.creative.weatherapp_bable.utils.Constants.IMG_URL

class WeatherDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val weather = intent.getSerializableExtra("weatherList") as? WeatherDetail
        if (weather != null) {
            showDetail(weather)
        } else {
            // Handle the case where 'weather' is null
        }
    }

    private fun showDetail(weather: WeatherDetail) {
        binding.apply {
            temperature.text = (weather.main.temp / 10).toString()
            textViewTemperature.text = (weather.main.temp / 10).toString()
            textviewWind.text = weather.main.humidity.toString()
            textViewWin2.text = weather.main.pressure.toString()
            textviewWindd.text = weather.clouds.all.toString()
            description.text = weather.weather!![0].description
        }
        binding.back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        Glide.with(this).load(IMG_URL+weather.weather!![0].icon+ Constants.IMG_EXT).into(binding.imageViewWeatherIcon)
    }
}