package com.coding.weather.fragments.viewModel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coding.weather.helper.getCountryCode
import com.dil.singles.helper.SingleEvent

class SearchViewModel : ViewModel() {
    val cityNameEt = MutableLiveData("")
    val zipcodeEt = MutableLiveData("")
    val countryNameEt = MutableLiveData("")
    val countryCode = MutableLiveData("")
    val onBack = SingleEvent()
    val searchByCity = ObservableBoolean(false)
    val searchByZip = ObservableBoolean(false)
    val searchByCountry = ObservableBoolean(false)
    val cityEvent = SingleEvent()
    val cityCountryEvent = SingleEvent()
    val zipCountryEvent = SingleEvent()
    val cityBtn = ObservableBoolean(false)
    val countryBtn = ObservableBoolean(false)
    val zipBtn = ObservableBoolean(false)

    // TODO: Click listener for search using city
    fun onCitySearch() {
        if (cityNameEt.value?.length != 0) {
            cityEvent.actionOccured()
        }
    }

    // TODO: Click listener for search using city and country
    fun onCityCountrySearch() {
        if (cityNameEt.value?.length != 0 && countryNameEt.value?.length != 0) {
            countryCode.value = getCountryCode(countryNameEt.value.toString().trim())
            cityCountryEvent.actionOccured()
        }
    }

    // TODO: Click listener for search using Zip code and country
    fun onZipCountrySearch() {
        if (zipcodeEt.value?.length != 0 && countryNameEt.value?.length != 0) {
            countryCode.value = getCountryCode(countryNameEt.value.toString().trim())
            zipCountryEvent.actionOccured()
        }
    }

    // TODO: Click listener for onBack Click
    fun onBackClick() {
        onBack.actionOccured()
    }
}