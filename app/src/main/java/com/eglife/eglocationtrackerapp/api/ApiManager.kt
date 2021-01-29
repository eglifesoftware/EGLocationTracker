package com.eglife.eglocationtrackerapp.api

import com.eglife.eglocationtrackerapp.api.base.BaseApiClient
import com.eglife.eglocationtrackerapp.api.base.json.LongTypeAdapter
import com.google.gson.GsonBuilder

class ApiManager {
    companion object {
        fun getService() : CoreApiService {
            val baseUrl = "http://utility.eglifesoftware.com/api/"

            val builder: GsonBuilder = GsonBuilder()
                .registerTypeAdapter(Long::class.java, LongTypeAdapter())
            return BaseApiClient(baseUrl).create(builder.create())
        }
    }
}