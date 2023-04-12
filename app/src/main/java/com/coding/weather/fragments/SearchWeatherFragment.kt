package com.coding.weather.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.coding.weather.R
import com.coding.weather.databinding.FragmentSearchWeatherBinding
import com.coding.weather.fragments.viewModel.SearchViewModel
import com.coding.weather.helper.Constants.CITY
import com.coding.weather.helper.Constants.COUNTRY
import com.coding.weather.helper.Constants.KEY
import com.coding.weather.helper.Constants.SEARCHBYCITY
import com.coding.weather.helper.Constants.SEARCHBYCITYANDCOUNTRY
import com.coding.weather.helper.Constants.SEARCHBYZIPCOUNTRY
import com.coding.weather.helper.Constants.ZIPCODE

class SearchWeatherFragment : Fragment() {

    // TODO: Declaring Binding and ViewModel
    private var searchBinding: FragmentSearchWeatherBinding? = null
    private val binding get() = searchBinding!!
    private val weatherViewModel: SearchViewModel by viewModels()
    private var arg: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchWeatherBinding.inflate(layoutInflater, container, false)
        binding.viewModel = weatherViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvents()
        getData()
    }

    // TODO: Getting data from the previous fragment to identity which component need to show on the screen
    private fun getData() {
        arg = arguments?.getString(KEY)
        if (arg.equals(SEARCHBYCITY)) {
            weatherViewModel.searchByCity.set(true)
            weatherViewModel.searchByZip.set(false)
            weatherViewModel.searchByCountry.set(false)
            weatherViewModel.cityBtn.set(true)
            weatherViewModel.countryBtn.set(false)
            weatherViewModel.zipBtn.set(false)
        } else if (arg.equals(SEARCHBYCITYANDCOUNTRY)) {
            weatherViewModel.searchByCity.set(true)
            weatherViewModel.searchByZip.set(false)
            weatherViewModel.searchByCountry.set(true)
            weatherViewModel.cityBtn.set(false)
            weatherViewModel.countryBtn.set(true)
            weatherViewModel.zipBtn.set(false)
        } else if (arg.equals(SEARCHBYZIPCOUNTRY)) {
            weatherViewModel.searchByCity.set(false)
            weatherViewModel.searchByZip.set(true)
            weatherViewModel.searchByCountry.set(true)
            weatherViewModel.cityBtn.set(false)
            weatherViewModel.countryBtn.set(false)
            weatherViewModel.zipBtn.set(true)
        }
    }

    // TODO: Adding Click events
    private fun clickEvents() {

        weatherViewModel.onBack.consume(viewLifecycleOwner)
        {
            findNavController().popBackStack()
        }

        weatherViewModel.cityEvent.consume(viewLifecycleOwner)
        {
            val bundle = Bundle()
            bundle.putString(CITY, weatherViewModel.cityNameEt.value.toString())
            bundle.putString(KEY, SEARCHBYCITY)
            findNavController().navigate(R.id.nav_home_fragment, bundle)
        }

        weatherViewModel.cityCountryEvent.consume(viewLifecycleOwner)
        {
            val bundle = Bundle()
            bundle.putString(CITY, weatherViewModel.cityNameEt.value.toString())
            bundle.putString(COUNTRY, weatherViewModel.countryCode.value.toString())
            bundle.putString(KEY, SEARCHBYCITYANDCOUNTRY)
            findNavController().navigate(R.id.nav_home_fragment, bundle)
        }

        weatherViewModel.zipCountryEvent.consume(viewLifecycleOwner)
        {
            val bundle = Bundle()
            bundle.putString(ZIPCODE, weatherViewModel.zipcodeEt.value.toString())
            bundle.putString(COUNTRY, weatherViewModel.countryCode.value.toString())
            bundle.putString(KEY, SEARCHBYZIPCOUNTRY)
            findNavController().navigate(R.id.nav_home_fragment, bundle)
        }
    }
}