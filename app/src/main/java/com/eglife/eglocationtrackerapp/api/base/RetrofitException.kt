package com.eglife.eglocationtrackerapp.api.base

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class RetrofitException(private val errorMessage: String, private val exception: Throwable) : RuntimeException(errorMessage, exception) {
    private var url: String? = null
    private var response: Response<*>? = null
    private var kind: Kind? = null
    private var retrofit: Retrofit? = null

    constructor(message: String,
                exception: Throwable,
                url: String?,
                response: Response<*>?,
                kind: Kind,
                retrofit: Retrofit?) : this(message, exception) {
        this.url = url
        this.response = response
        this.kind = kind
        this.retrofit = retrofit
    }

    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Type): T? {
        if (response == null || response?.errorBody() == null) {
            return null
        }

        val converter: Converter<ResponseBody?, T?>? = retrofit?.responseBodyConverter(type, arrayOfNulls<Annotation>(0))
        return converter?.convert(response?.errorBody())
    }

    enum class Kind {
        NETWORK,
        HTTP,
        UNEXPECTED
    }
}