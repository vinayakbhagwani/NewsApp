package com.vinayak.apps.cardstacksdemoapp

import com.vinayak.apps.cardstacksdemoapp.data.RetrofitInstance
import com.vinayak.apps.cardstacksdemoapp.dto.NewsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(RetrofitInstance.END_POINT)
    suspend fun getNewsData(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsApiResponse>

}