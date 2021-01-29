package com.eglife.eglocationtracker

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocationTrackerService: Service() {
    private val locationTracker = LocationTracker()

    override fun onCreate() {
        super.onCreate()
        locationTracker.startLocationTracking(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY_COMPATIBILITY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}