package com.example.todoapp.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder()
        .add(BooleanAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://todos.simpleapi.dev")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
