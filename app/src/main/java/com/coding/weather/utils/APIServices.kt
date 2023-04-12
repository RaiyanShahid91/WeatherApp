package com.coding.weather.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val IMAGE_BASE_URL = "https://openweathermap.org/img/wn/"

object APIServices {
    val apiServices: WeatherApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiServices = retrofit.create(WeatherApi::class.java)
    }
}