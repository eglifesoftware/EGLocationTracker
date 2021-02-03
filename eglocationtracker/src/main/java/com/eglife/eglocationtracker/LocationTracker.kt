package com.eglife.eglocationtracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import io.reactivex.rxjava3.subjects.BehaviorSubject

class LocationTracker(private val intervalTime: Long = 10000,
                      private val fastestIntervalTime: Long = 5000,
                      val trackerPriority: Int = LocationTrackerPriority.PRIORITY_HIGH_ACCURACY.value,
                      private val smallestDistance: Float = 0f) {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null
    var isRunningLocationUpdate = false

    val locationObs: BehaviorSubject<Optional<EGLocation>> = BehaviorSubject.createDefault(Optional(null))

    fun startLocationTracking(context: Context) {
        // Create fusedLocation client if needed
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }

        // Create location request if needed
        if (locationRequest == null) {
            locationRequest = createLocationRequest(context)
        }

        // Check location setting
        locationRequest?.let {
            checkLocationSetting(it, context)
        }
    }

    fun stopLocationTracking() {
        stopLocationUpdates()
    }

    private fun createLocationRequest(context: Context) : LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = intervalTime
            fastestInterval = fastestIntervalTime
            priority = trackerPriority
            smallestDisplacement = smallestDistance
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient?.lastLocation
                ?.addOnSuccessListener { location : Location? ->
                    Log.e("iii", "Location: ${location.toString()}")
                }
    }

    private fun checkLocationSetting(locationRequest: LocationRequest, context: Context) {
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            Log.e("iii", locationSettingsResponse.toString())
            startGettingLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(context as Activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error
                }
            }
        }
    }

    private fun startGettingLocation() {
        Log.e("iii", "Start location update")
        startLocationUpdates()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode != REQUEST_CHECK_SETTINGS) return

        when(resultCode) {
            Activity.RESULT_OK -> {
                Log.e("iii", "Allow location permission")
                startGettingLocation()
            }

            Activity.RESULT_CANCELED -> {
                Log.e("iii", "Deny location permission")
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                Log.e("iii","Location update: ${location.toString()}")
                locationObs.onNext(Optional(EGLocation(location.latitude, location.longitude)))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (isRunningLocationUpdate) return

        isRunningLocationUpdate = true
        fusedLocationClient?.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        isRunningLocationUpdate = false
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 1001
    }
}