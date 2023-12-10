package com.creative.weatherapp_bable

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creative.weatherapp_bable.databinding.ActivityMainBinding
import com.creative.weatherapp_bable.model.WeatherDetail
import com.creative.weatherapp_bable.utils.AddCountryDialog
import com.creative.weatherapp_bable.utils.ConnectivityStatus
import com.creative.weatherapp_bable.utils.Constants.API_KEYS
import com.creative.weatherapp_bable.utils.Constants.DEFAULT_COUNTRY
import com.creative.weatherapp_bable.utils.LocationHelper
import com.creative.weatherapp_bable.utils.Utils.showToast
import com.creative.weatherapp_bable.view.WeatherDetailActivity
import com.creative.weatherapp_bable.view.adapter.WeatherAdapter
import com.creative.weatherapp_bable.viewModel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import com.creative.weatherapp_bable.utils.Result

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private var adapter: WeatherAdapter? = null
    private lateinit var autoCompleteAdapter : ArrayAdapter<String>
    private var weatherList: ArrayList<WeatherDetail> = ArrayList()
    private val countryList = mutableListOf("Tunis", "CasaBlanca", "Dubai", "Morocco", "France")
    private var currentCountry=""
    private lateinit var locationHelper: LocationHelper
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val connectivityLiveData = ConnectivityStatus(this)
        connectivityLiveData.observe(this) { isAvailable ->
            when (isAvailable) {
                true -> initData()
                else -> Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initData() {

        handleClickListener()
        locationHelper = LocationHelper(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        adapter = WeatherAdapter(onPictureClickListener = {
            val intent = Intent(this, WeatherDetailActivity::class.java)
            intent.putExtra("weatherList", it)
            startActivity(intent)
        })

        binding.recyclerviewSearch.adapter = adapter
        binding.recyclerviewSearch.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, countryList)

        binding.country.setAdapter(autoCompleteAdapter)

        binding.country.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            currentCountry = selectedItem
            lifecycleScope.launch(Dispatchers.Main) { getWeather(selectedItem) }
        }

        currentCountry = DEFAULT_COUNTRY
        lifecycleScope.launch(Dispatchers.Main) {
            getWeather(currentCountry)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun handleClickListener() {
        binding.addCountry.setOnClickListener {
            showAddCountryDialog(false)
        }
        binding.removeCountry.setOnClickListener {
            showAddCountryDialog(true)
        }

        binding.refresh.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) { getWeather(currentCountry) }
        }

        binding.locationBtn.setOnClickListener {
            getLocation()
        }

        binding.confirmSearch.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                getWeather(binding.country.text.toString())
            }
        }
    }

    private fun showAddCountryDialog(shouldRemove:Boolean) {
        val addCountryDialog = AddCountryDialog(this) { newCountry ->
            addRemoveCountry(newCountry,shouldRemove) }
            addCountryDialog.show()
    }

    private fun addRemoveCountry(newCountry: String, shouldRemove: Boolean) {
        if (shouldRemove) {
            if (countryList.contains(newCountry)) {
                countryList.remove(newCountry)
                showToast(this, getString(R.string.removed_country) + newCountry)
            } else {
                showToast(this, getString(R.string.country_not_found) + newCountry)
            }
        } else {
            if (!countryList.contains(newCountry)) {
                countryList.add(newCountry)
                showToast(this, getString(R.string.added_country) + newCountry)
            } else {
                showToast(this, getString(R.string.added_country_exist) + newCountry)
            }
        }
        autoCompleteAdapter.clear()
        autoCompleteAdapter.addAll(countryList)
        autoCompleteAdapter.notifyDataSetChanged()
        binding.country.setAdapter(autoCompleteAdapter)

    }

    private suspend fun getWeather(country:String) {

        viewModel.getWeatherList(country,API_KEYS).observe(this) {result ->
            when (result) {
                is Result.Success -> {

                    weatherList.clear()
                    weatherList.addAll(listOf(result.data))
                    binding.refresh.visibility = View.VISIBLE
                    binding.noflight.visibility = View.GONE
                    binding.recyclerviewSearch.visibility = View.VISIBLE
                    adapter?.setWeather(weatherList)
                }
                is Result.Error -> {

                    binding.refresh.visibility = View.GONE
                    binding.noflight.visibility = View.VISIBLE
                    binding.recyclerviewSearch.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (locationHelper.isLocationEnabled() && locationHelper.checkPermissions()) {

            mFusedLocationClient.lastLocation.addOnCompleteListener(this) {
                val location: Location? = it.result
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val list: List<Address> = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1)!!
                    currentCountry =  list[0].countryName
                    binding.country.setText(currentCountry)
                    lifecycleScope.launch(Dispatchers.Main) { getWeather(currentCountry) }

                }
            }
        }  else {
            locationHelper.requestPermissions()
        }
    }

}