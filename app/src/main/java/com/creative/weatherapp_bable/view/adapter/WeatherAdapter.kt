package com.creative.weatherapp_bable.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.creative.weatherapp_bable.databinding.WeatherItemBinding
import com.creative.weatherapp_bable.model.WeatherDetail
import com.creative.weatherapp_bable.utils.Constants.IMG_EXT
import com.creative.weatherapp_bable.utils.Constants.IMG_URL

class WeatherAdapter constructor(val onPictureClickListener: ((url: WeatherDetail?) -> Unit)) : RecyclerView.Adapter<MainViewHolder>() {
    private lateinit var context: Context
    private var weatherDetails = ArrayList<WeatherDetail>()

    fun setWeather(weatherList: ArrayList<WeatherDetail>) {
        this.weatherDetails = weatherList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeatherItemBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val dataList = weatherDetails[holder.adapterPosition]

        Glide.with(context).load(IMG_URL + dataList.weather!![0].icon+IMG_EXT).into(holder.binding.weatherPicture)
        holder.binding.apply {
            countryName.text = dataList.name
            description.text = dataList.weather!![0].description
            humidity.text = dataList.main.humidity.toString()
            temperature.text = dataList.main.temp.toString()
        }
       holder.binding.layoutcell.setOnClickListener {
            onPictureClickListener.invoke(dataList)
        }
    }

    override fun getItemCount(): Int {
        return weatherDetails.size
    }
}
    class MainViewHolder(bindingArg: WeatherItemBinding) : RecyclerView.ViewHolder(bindingArg.root) {
    val binding = bindingArg
}