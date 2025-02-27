package com.vinayak.apps.cardstacksdemoapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.vinayak.apps.cardstacksdemoapp.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

object RetrofitInstance {

    const val BASE_URL = "https://newsapi.org/"
    const val END_POINT = "v2/everything"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getInstance(): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val okHttpClient = OkHttpClient
            .Builder()
            .connectTimeout(Duration.ofMinutes(1))
            .readTimeout(Duration.ofMinutes(1))
            .writeTimeout(Duration.ofMinutes(1))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }

    fun createApi(): ApiService {
        return getInstance().create(ApiService::class.java)
    }
}