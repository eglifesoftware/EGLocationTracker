package com.eglife.eglocationtrackerapp.api.base

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.observable.ObservableSingleSingle
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type


class RxCoreHandlingCallAdapterFactory : CallAdapter.Factory() {
    private var original: RxJava3CallAdapterFactory? = RxJava3CallAdapterFactory.create()

    companion object {
        @JvmStatic @JvmName("create")
        fun create(): CallAdapter.Factory {
            return RxCoreHandlingCallAdapterFactory()
        }
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        return RxCallAdapterWrapper(retrofit, original?.get(returnType, annotations, retrofit))
    }
}

class RxCallAdapterWrapper<R>(retrofit: Retrofit, wrapper: CallAdapter<R, *>?) : CallAdapter<R, Single<Any>> {
    private var retrofit: Retrofit? = retrofit
    private var wrapped: CallAdapter<R, *>? = wrapper

    override fun responseType(): Type {
        return wrapped!!.responseType()
    }

    @Suppress("UNCHECKED_CAST")
    override fun adapt(call: Call<R>): Single<Any>? {
        return (wrapped?.adapt(call) as ObservableSingleSingle<Any>).onErrorResumeNext {
            ObservableSingleSingle.error(asRetrofitException(it))
        }
    }

    private fun asRetrofitException(throwable: Throwable) : RetrofitException {
        return when (throwable) {
            is HttpException -> {
                val response = throwable.response()
                val message: String = response?.code().toString() + " " + response?.message()
                val url = response?.raw()?.request?.url.toString()
                RetrofitException(message, throwable, url, response, RetrofitException.Kind.HTTP, retrofit)
            }
            is IOException -> {
                RetrofitException(throwable.message
                        ?: "", throwable, null, null, RetrofitException.Kind.NETWORK, null)
            }
            else -> RetrofitException(throwable.message
                    ?: "", throwable, null, null, RetrofitException.Kind.UNEXPECTED, null)
        }
    }
}