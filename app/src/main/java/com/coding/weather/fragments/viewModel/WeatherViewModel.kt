package com.coding.weather.fragments.viewModel

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.coding.weather.R
import com.coding.weather.model.Example
import com.coding.weather.utils.APIServices
import com.coding.weather.utils.IMAGE_BASE_URL
import com.coding.weather.helper.Constants
import com.coding.weather.helper.SharedPrefData
import com.coding.weather.helper.convertTime
import com.dil.singles.helper.SingleEvent
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    val city = MutableLiveData("")
    val temp = MutableLiveData("")
    val weather_quality = MutableLiveData("")
    val sunrise = MutableLiveData("")
    val sunset = MutableLiveData("")
    val humidity = MutableLiveData("")
    val visibilty = MutableLiveData("")
    val max_temp = MutableLiveData("")
    val min_temp = MutableLiveData("")
    val temp_icon = MutableLiveData("")
    val showLoader = ObservableBoolean(false)
    val searchByCity = SingleEvent()
    val searchByCityAndCountry = SingleEvent()
    val searchByCityStateCountry = SingleEvent()

    fun getWeatherByCity(cityStr: String, apiKey: String, context: Context) {
        showLoader.set(true)
        val call: Call<Example> = APIServices.apiServices.getWeatherByCity(cityStr, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                if (response.code() == 404) {
                    showLoader.set(false)
                    Toast.makeText(context, "Please enter a valid City", Toast.LENGTH_LONG).show()
                } else {
                    showLoader.set(false)
                    val data = response.body()
                    val main = data?.main
                    val d = main?.temp
                    val temperature = (d?.minus(273.15))?.toInt()
                    temp.value = temperature.toString()
                    temp_icon.value = data?.weather?.get(0)?.icon
                    weather_quality.value = data?.weather?.get(0)?.main
                    city.value = data?.name
                    sunrise.value = data?.sys?.sunrise?.toLong()?.let { convertTime(it) }
                    sunset.value = data?.sys?.sunset?.toLong()?.let { convertTime(it) }
                    humidity.value = "${data?.main?.humidity.toString()} %"
                    visibilty.value = data?.visibility.toString()
                    val max = main?.tempMax
                    val min = main?.tempMin
                    max_temp.value = (max?.minus(273.15))?.toInt().toString()+"°C"
                    min_temp.value = (min?.minus(273.15))?.toInt().toString()+"°C"

                }
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                showLoader.set(false)
                Log.i("onFailure", t.toString())
            }

        })
    }

    fun getWeatherByZip(zip: String, apiKey: String, context: Context) {
        showLoader.set(true)
        val call: Call<Example> = APIServices.apiServices.getWeatherByZip(zip, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                if (response.code() == 404) {
                    showLoader.set(false)
                    Toast.makeText(context, "Please enter a valid City", Toast.LENGTH_LONG).show()
                } else {
                    showLoader.set(false)
                    val data = response.body()
                    val main = data?.main
                    val d = main?.temp
                    val temperature = (d?.minus(273.15))?.toInt()
                    temp.value = temperature.toString()
                    weather_quality.value = data?.weather?.get(0)?.main
                    city.value = data?.name
                    sunrise.value = data?.sys?.sunrise?.toLong()?.let { convertTime(it) }
                    sunset.value = data?.sys?.sunset?.toLong()?.let { convertTime(it) }
                    temp_icon.value = data?.weather?.get(0)?.icon
                    humidity.value = "${data?.main?.humidity.toString()} %"
                    visibilty.value = data?.visibility.toString()
                    val max = main?.tempMax
                    val min = main?.tempMin
                    max_temp.value = (max?.minus(273.15))?.toInt().toString()+"°C"
                    min_temp.value = (min?.minus(273.15))?.toInt().toString()+"°C"
                    SharedPrefData(context).saveDataString(
                        Constants.SAVEDCITY,
                        city.value.toString().trim()
                    )
                }
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                showLoader.set(false)
                Log.i("onFailure", t.toString())
            }

        })
    }

    fun getWeatherByLatLong(lat: String, longi: String, apiKey: String, context: Context) {
        showLoader.set(true)
        val call: Call<Example> = APIServices.apiServices.getWeatherByLatLong(lat, longi, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                if (response.code() == 404) {
                    showLoader.set(false)
                    Toast.makeText(context, "Something went wrong.\nTry again.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    showLoader.set(false)
                    val data = response.body()
                    val main = data?.main
                    val d = main?.temp
                    val temperature = (d?.minus(273.15))?.toInt()
                    temp_icon.value = data?.weather?.get(0)?.icon
                    temp.value = temperature.toString()
                    weather_quality.value = data?.weather?.get(0)?.main
                    city.value = data?.name
                    sunrise.value = data?.sys?.sunrise?.toLong()?.let { convertTime(it) }
                    sunset.value = data?.sys?.sunset?.toLong()?.let { convertTime(it) }
                    humidity.value = "${data?.main?.humidity.toString()} %"
                    visibilty.value = data?.visibility.toString()
                    val max = main?.tempMax
                    val min = main?.tempMin
                    max_temp.value = (max?.minus(273.15))?.toInt().toString()+"°C"
                    min_temp.value = (min?.minus(273.15))?.toInt().toString()+"°C"
                }
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                showLoader.set(false)
                Log.i("onFailure", t.toString())
            }

        })
    }

    fun getIconLL(
        lat: String,
        longi: String,
        apiKey: String,
        context: Context,
        imageView: ImageView
    ) {
        val call: Call<Example> = APIServices.apiServices.getWeatherByLatLong(lat, longi, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                val data = response.body()
                temp_icon.value = data?.weather?.get(0)?.icon
                var url = "$IMAGE_BASE_URL${temp_icon.value.toString().trim()}@2x.png"
                Glide.with(context).load(url).fitCenter()
                    .into(imageView)

            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                Log.i("onFailure", t.toString())
            }

        })
    }

    fun getIcon(city: String, apiKey: String, context: Context, imageView: ImageView) {
        val call: Call<Example> = APIServices.apiServices.getWeatherByCity(city, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                val data = response.body()
                temp_icon.value = data?.weather?.get(0)?.icon
                var url = "$IMAGE_BASE_URL${temp_icon.value.toString().trim()}@2x.png"
                Glide.with(context).load(url).fitCenter()
                    .into(imageView)
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                Log.i("onFailure", t.toString())
            }

        })
    }

    fun getIconZ(zip: String, apiKey: String, context: Context, imageView: ImageView) {
        val call: Call<Example> = APIServices.apiServices.getWeatherByZip(zip, apiKey)
        call.enqueue(object : Callback<Example> {
            override fun onResponse(call: Call<Example>, response: Response<Example>) {
                val data = response.body()
                temp_icon.value = data?.weather?.get(0)?.icon
                var url = "$IMAGE_BASE_URL${temp_icon.value.toString().trim()}@2x.png"
                Glide.with(context).load(url).fitCenter()
                    .into(imageView)
            }

            override fun onFailure(call: Call<Example>, t: Throwable?) {
                Log.i("onFailure", t.toString())
            }

        })
    }

    fun searchBy(context: Context) {
        val bottomSheetDialog =
            BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        val bottomSheetView: View = LayoutInflater.from(context).inflate(
            R.layout.layout_searchby,
            bottomSheetDialog.findViewById(R.id.bottomsheetSearchBY)
        )

        bottomSheetView.findViewById<TextView>(R.id.cityname_tv).setOnClickListener {
            searchByCity.actionOccured()
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<TextView>(R.id.citycountry_tv).setOnClickListener {
            searchByCityAndCountry.actionOccured()
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<TextView>(R.id.citystatecountry_tv).setOnClickListener {
            searchByCityStateCountry.actionOccured()
            bottomSheetDialog.dismiss()
        }
        bottomSheetView.findViewById<AppCompatButton>(R.id.closeBtn).setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

}