package com.creative.weatherapp_bable.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.creative.weatherapp_bable.model.WeatherDetail
import com.creative.weatherapp_bable.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.creative.weatherapp_bable.utils.Result

@HiltViewModel
class WeatherViewModel @Inject constructor(private val mainRepo: MainRepository) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable -> throwable.printStackTrace() }
    val errorMessage = MutableLiveData<String>()
    private var weatherList = MutableLiveData<WeatherDetail>()

    suspend fun getWeatherList(country:String, apikey:String):LiveData<Result<WeatherDetail>> {
        val resultLiveData = MutableLiveData<Result<WeatherDetail>>()

        try {
            val response = mainRepo.getWeatherDetail(country, apikey)
            withContext(Dispatchers.Main + coroutineExceptionHandler) {
                val responseBody = response.body()
                if (responseBody != null) {
                    resultLiveData.value = Result.Success(responseBody)
                } else {
                    resultLiveData.value = Result.Error("Response body is null.")
                }
            }
        } catch (e: Exception) {
            resultLiveData.value = Result.Error("Error: ${e.message}")
        }

        return resultLiveData
    }

}