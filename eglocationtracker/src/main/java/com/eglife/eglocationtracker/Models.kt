package com.eglife.eglocationtracker

import com.google.android.gms.location.LocationRequest

data class EGLocation(
    val latitude: Double? = null,
    val longitude: Double? = null
)

enum class LocationTrackerPriority(val value: Int) {
    PRIORITY_HIGH_ACCURACY(LocationRequest.PRIORITY_HIGH_ACCURACY),
    PRIORITY_BALANCED_POWER_ACCURACY(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
    PRIORITY_LOW_POWER(LocationRequest.PRIORITY_LOW_POWER),
    PRIORITY_NO_POWER(LocationRequest.PRIORITY_NO_POWER)
}

class Optional<T>(val value: T?) {
    companion object {
        fun <T> empty(): Optional<T> = Optional(null)
        fun <T> (Optional<T>).isEmpty(): Boolean = this.value == null
    }
}