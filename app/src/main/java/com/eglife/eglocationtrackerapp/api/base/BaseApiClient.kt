package com.eglife.eglocationtrackerapp.api.base

import android.util.Log
import androidx.annotation.NonNull
import com.eglife.eglocationtrackerapp.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class BaseApiClient {
    var baseUrl: String = ""

    constructor()

    constructor(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    inline fun <reified T> create(gson: Gson?): T {
        val gsonConfig = gson ?: GsonBuilder().create()

        val clientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(@NonNull message: String) {
                    Log.i("iii", "[okhttp retrofit] $message")
                }
            })
            logging.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(logging)
        }

        val client = clientBuilder.build()
        val retrofit = Retrofit.Builder()
            .client(client)
            //.client(okHttpClient)
            .addCallAdapterFactory(RxCoreHandlingCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonConfig))
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(T::class.java)
    }

//    val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(60, TimeUnit.SECONDS)
//        .writeTimeout(60, TimeUnit.SECONDS)
//        .readTimeout(60, TimeUnit.SECONDS)
//        .build()
}