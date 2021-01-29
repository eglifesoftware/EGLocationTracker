package com.eglife.eglocationtrackerapp.api.base

import com.google.gson.annotations.SerializedName

class BaseServiceResponse<T>(@SerializedName(value = "data", alternate = ["GroupCodes"]) val data: T,
                             @SerializedName("status") val status : Int,
                             @SerializedName("message") val message : String) {
    override fun toString(): String = "BaseServiceResponse{" +
            "data=" + data +
            ", status=" + status +
            ", message='" + message + '\'' +
            '}'
}