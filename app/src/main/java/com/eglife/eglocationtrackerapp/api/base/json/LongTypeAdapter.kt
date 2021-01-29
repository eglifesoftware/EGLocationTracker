package com.eglife.eglocationtrackerapp.api.base.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException


class LongTypeAdapter : TypeAdapter<Long?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Long? {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val stringValue: String = reader.nextString()
        return try {
            java.lang.Long.valueOf(stringValue)
        } catch (e: NumberFormatException) {
            null
        }
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Long?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.value(value)
    }
}