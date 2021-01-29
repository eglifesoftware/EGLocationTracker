package com.eglife.eglocationtrackerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.eglife.eglocationtracker.EGLocation
import com.eglife.eglocationtracker.LocationTracker
import com.eglife.eglocationtracker.LocationTrackerPriority
import com.eglife.eglocationtrackerapp.api.ApiManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

const val REQUEST_LOCATION_PERMISSION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() {
    private val locationTracker = LocationTracker(intervalTime = 2000,
            fastestIntervalTime = 1000,
            trackerPriority = LocationTrackerPriority.PRIORITY_HIGH_ACCURACY.value,
            smallestDistance = 0f)

    val tickHandler = Handler(Looper.getMainLooper())
    private val tick: Runnable = object: Runnable {
        override fun run() {
            Log.e("iii", "Tick ...")
            tickHandler.postDelayed(this, 1000)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission()
        } else {
            requestPermissionDone()
        }

        //tickHandler.postDelayed(tick, 1000)
    }

    private fun requestPermissionDone() {
        Log.e("iii", "Start location tracking")
        //startService(Intent(this, LocationTrackerService::class.java))
        locationTracker.startLocationTracking(this)

        locationTracker.locationObs
            .observeOn(AndroidSchedulers.mainThread())
            .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe {
                it.value?.let { location ->
                    sendLocation(location)
                }
            }
    }

    private fun requestPermissionFail() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionDone()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                requestPermissionDone()
            }

            else -> {
                // You can directly ask for the permission.
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            REQUEST_LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestPermissionDone()
                } else {
                    requestPermissionFail()
                }
            }

            else -> {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            locationTracker.handleActivityResult(requestCode, resultCode)
            super.onActivityResult(requestCode, resultCode, data)
        }

    private fun sendLocation(location: EGLocation) {
        Log.e("iii", "Send location: $location")
        ApiManager.getService()
            .sendLocation(location.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe({
            }, {e ->
            })
    }
}