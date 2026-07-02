package com.kotlin.dvijaypatient.network

import com.google.gson.GsonBuilder
import com.kotlin.dvijaypatient.global.ClassGlobal.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    var gson = GsonBuilder()
        .setLenient()
        .create()


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
          //  .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}