package com.coding.weather.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.coding.weather.R
import com.coding.weather.databinding.FragmentWeatherBinding
import com.coding.weather.fragments.viewModel.WeatherViewModel
import com.coding.weather.helper.Constants.CITY
import com.coding.weather.helper.Constants.COUNTRY
import com.coding.weather.helper.Constants.KEY
import com.coding.weather.helper.Constants.SAVEDCITY
import com.coding.weather.helper.Constants.SEARCHBYCITY
import com.coding.weather.helper.Constants.SEARCHBYCITYANDCOUNTRY
import com.coding.weather.helper.Constants.SEARCHBYZIPCOUNTRY
import com.coding.weather.helper.Constants.ZIPCODE
import com.coding.weather.helper.SharedPrefData
import com.coding.weather.helper.isLocationEnabled
import com.google.android.gms.location.*
import java.util.*

@Suppress("DEPRECATION")
class WeatherFragment : Fragment() {

    // TODO: Declaring Binding and ViewModel
    private var weatherBinding: FragmentWeatherBinding? = null
    private val binding get() = weatherBinding!!
    private val weatherViewModel: WeatherViewModel by viewModels()
    var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        weatherBinding = FragmentWeatherBinding.inflate(layoutInflater, container, false)
        binding.viewModel = weatherViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImage()
        clickEvents()
        getData()
    }

    // TODO: Getting data from the previous fragment to show the search wether result
    private fun getData() {
        val city = arguments?.getString(CITY)
        val country = arguments?.getString(COUNTRY)
        val zipcode = arguments?.getString(ZIPCODE)
        val key = arguments?.getString(KEY)

        if (key.equals(SEARCHBYCITY)) {
            context?.let {
                if (city != null) {
                    weatherViewModel.getWeatherByCity(
                        city, getString(R.string.apiKey),
                        it
                    )
                    weatherViewModel.getIcon(
                        city,
                        getString(R.string.apiKey),
                        requireContext(), binding.tempIcon
                    )
                    SharedPrefData(requireContext()).saveDataString(SAVEDCITY, city)
                }
            }
        } else if (key.equals(SEARCHBYCITYANDCOUNTRY)) {
            context?.let {
                if (city != null && country != null) {
                    weatherViewModel.getWeatherByCity(
                        "$city,$country", getString(R.string.apiKey),
                        it
                    )
                    weatherViewModel.getIcon(
                        city,
                        getString(R.string.apiKey),
                        requireContext(), binding.tempIcon
                    )
                    SharedPrefData(requireContext()).saveDataString(SAVEDCITY, city)
                }
            }
        } else if (key.equals(SEARCHBYZIPCOUNTRY)) {
            context?.let {
                if (zipcode != null && country != null) {
                    weatherViewModel.getWeatherByZip(
                        "$zipcode,$country", getString(R.string.apiKey),
                        it
                    )
                    weatherViewModel.getIconZ(
                        "$zipcode,$country",
                        getString(R.string.apiKey),
                        requireContext(), binding.tempIcon
                    )
                }
            }
        }
    }

    private fun clickEvents() {
        weatherViewModel.searchByCity.consume(viewLifecycleOwner)
        {
            val bundle = Bundle()
            bundle.putString(KEY, SEARCHBYCITY)
            findNavController().navigate(R.id.nav_search_fragment, bundle)
        }
        weatherViewModel.searchByCityAndCountry.consume(viewLifecycleOwner)
        {
            val bundle = Bundle()
            bundle.putString(KEY, SEARCHBYCITYANDCOUNTRY)
            findNavController().navigate(R.id.nav_search_fragment, bundle)
        }

        weatherViewModel.searchByCityStateCountry.consume(viewLifecycleOwner)
        {
            val bundle = Bundle()
            bundle.putString(KEY, SEARCHBYZIPCOUNTRY)
            findNavController().navigate(R.id.nav_search_fragment, bundle)
        }

    }

    private fun setImage() {
        val c: Calendar = Calendar.getInstance()
        when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> {
                weatherBinding?.parentLayout?.setBackgroundResource(R.drawable.morning_img)
            }
            in 12..15 -> {
                weatherBinding?.parentLayout?.setBackgroundResource(R.drawable.afternoon_img)
            }
            in 16..20 -> {
                weatherBinding?.parentLayout?.setBackgroundResource(R.drawable.evening_img)
            }
            in 21..23 -> {
                weatherBinding?.parentLayout?.setBackgroundResource(R.drawable.night_img)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val key = arguments?.getString(KEY)
        if (key == null) {
            getLastLocation()
        }
    }

    // TODO:  Getting last location
    private fun getLastLocation() {
        if (context?.let { SharedPrefData(it).getDataString(SAVEDCITY) } != "") {
            weatherViewModel.getWeatherByCity(
                SharedPrefData(requireContext()).getDataString(SAVEDCITY),
                getString(R.string.apiKey),
                requireContext()
            )
            weatherViewModel.getIcon(
                SharedPrefData(requireContext()).getDataString(SAVEDCITY),
                getString(R.string.apiKey),
                requireContext(), binding.tempIcon
            )
        } else {
            mFusedLocationClient =
                context?.let { LocationServices.getFusedLocationProviderClient(it) }
            if (context?.let { isLocationEnabled(it) } == true) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        mFusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(requireContext())
                        val mLocationRequest = LocationRequest()
                        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        mLocationRequest.interval = 5
                        mLocationRequest.fastestInterval = 0
                        mLocationRequest.numUpdates = 1
                        mFusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(requireContext())
                        mFusedLocationClient!!.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                    } else {
                        weatherViewModel.getWeatherByLatLong(
                            location.latitude.toString().trim(),
                            location.longitude.toString().trim(),
                            getString(R.string.apiKey),
                            requireContext()
                        )
                        weatherViewModel.getIconLL(
                            location.latitude.toString().trim(),
                            location.longitude.toString().trim(),
                            getString(R.string.apiKey),
                            requireContext(), binding.tempIcon
                        )
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    // TODO:  LocationCallback for retrieving location
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                weatherViewModel.getWeatherByLatLong(
                    mLastLocation.latitude.toString().trim(),
                    mLastLocation.longitude.toString().trim(),
                    getString(R.string.apiKey),
                    requireContext()
                )
                weatherViewModel.getIconLL(
                    mLastLocation.latitude.toString().trim(),
                    mLastLocation.longitude.toString().trim(),
                    getString(R.string.apiKey),
                    requireContext(), binding.tempIcon
                )
            }
        }
    }

}