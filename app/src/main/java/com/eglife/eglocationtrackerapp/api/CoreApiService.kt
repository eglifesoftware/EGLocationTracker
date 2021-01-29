package com.eglife.eglocationtrackerapp.api

import com.eglife.eglocationtrackerapp.api.base.BaseServiceResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CoreApiService {
    @GET("sendLocation")
    fun sendLocation(@Query("location") local: String): Single<BaseServiceResponse<Unit>>

}