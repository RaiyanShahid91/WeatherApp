package com.coding.weather.activity

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.coding.weather.R


class MainActivity : AppCompatActivity() {
    
    private var isCourseLocationPermission = false
    private var isFineLocationPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("onCreate","onCreate")


        val buttonClick: Button = findViewById<View>(R.id.btn) as Button

        buttonClick.setOnClickListener { dialogBox() }

//        requestPermission()
    }

    private fun requestPermission() {
        val permissionLauncher: ActivityResultLauncher<Array<String>> =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isCourseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: isCourseLocationPermission
                isFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: isFineLocationPermission
            }

        allowMultiplePermission(permissionLauncher)
    }

    private fun allowMultiplePermission(permissionLauncher: ActivityResultLauncher<Array<String>>) {

        isCourseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


        val permissionRequest: MutableList<String> = ArrayList()


        if (!isFineLocationPermission) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!isCourseLocationPermission) {
            permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("OnStart","onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("OnResume","OnResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("onRestart","onRestart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPause","Onpause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("onStop","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy","onDestroy")
    }

    fun dialogBox()
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Do you want to exit ?")
        builder.setTitle("Alert !")
        builder.setCancelable(false)

        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                finish()
            } as DialogInterface.OnClickListener)


        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                dialog.cancel()
            } as DialogInterface.OnClickListener)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}