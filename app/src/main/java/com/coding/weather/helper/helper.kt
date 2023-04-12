package com.coding.weather.helper

import android.content.Context
import android.location.LocationManager
import java.text.SimpleDateFormat
import java.util.*

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun getCountryCode(countryName: String): String? {
    val isoCountryCodes = Locale.getISOCountries()
    for (code in isoCountryCodes) {
        val locale = Locale("", code)
        if (countryName.equals(locale.displayCountry, ignoreCase = true)) {
            return code
        }
    }
    return ""
}

fun convertTime(time: Long): String {
    val format = "h:mm a"
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(time * 1000))
}