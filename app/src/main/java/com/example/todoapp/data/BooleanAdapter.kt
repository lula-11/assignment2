package com.example.todoapp.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class BooleanAdapter {
    @FromJson
    fun fromJson(value: Any): Boolean {
        return when (value) {
            is Boolean -> value
            is Number -> value.toInt() != 0
            else -> throw IllegalArgumentException("Expected a boolean or number but was $value")
        }
    }

    @ToJson
    fun toJson(value: Boolean): Boolean {
        return value
    }
}
