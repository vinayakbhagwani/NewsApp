package com.vinayak.apps.cardstacksdemoapp

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplitude.core.Amplitude
import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import com.vinayak.apps.cardstacksdemoapp.data.RetrofitInstance
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsEntity
import com.vinayak.apps.cardstacksdemoapp.models.NewsArticle
import com.vinayak.apps.cardstacksdemoapp.utils.Helpers
import com.vinayak.apps.cardstacksdemoapp.utils.Helpers.TOTAL_MINUTES
import com.vinayak.apps.cardstacksdemoapp.utils.Helpers.verifyIfDbDeleteRequired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsListViewmodel @Inject constructor(
    val newsRepo: NewsRepository,
    val sharedPreferences: SharedPreferences,
    private val setupPeriodicWorkRequestUseCase: SetupPeriodicWorkRequestUseCase) : ViewModel() {

    val responseFlow = MutableStateFlow<List<NewsArticle>>(listOf())
    var job: Job? = null

    @Inject
    lateinit var amplitude: Amplitude

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("API_ERROR", "Error occurred: ${throwable.localizedMessage}")
    }

    fun fetchNews() {
        job = viewModelScope.launch(Dispatchers.Main + exceptionHandler) {
            try {
                newsRepo.getAllNewsList().collect { list ->
                    if (list.isNotEmpty()) {
                        if(verifyIfDbDeleteRequired(sharedPreferences) >= TOTAL_MINUTES) {
                            newsRepo.deleteAll()
                            sharedPreferences.edit().apply {
                                remove(Helpers.API_TIME)
                                apply()
                            }
                        } else {
                            val newsArticleList = list.map {
                                NewsArticle(
                                    title = it.title,
                                    description = it.description,
                                    image = it.image,
                                    newsUrl = it.newsUrl
                                )
                            }
                            responseFlow.emit(newsArticleList)
                            amplitude.track("DB_CALL_SUCCESSFUL")
                            if (job?.isActive == true) job?.cancel()
                        }
                    } else {
                        val response = RetrofitInstance.createApi().getNewsData(
                            query = "sports",
                            apiKey = "9ca9100a6da54f3297adb7d995257631"
                        )
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val newsArticleList = response.body()?.articles?.let {
                                    it.map {
                                        NewsArticle(
                                            title = it.title ?: "ABC News",
                                            image = it.urlToImage ?: "",
                                            description = it.description ?: "News Info",
                                            newsUrl = it.url ?: "https://www.google.com"
                                        )
                                    }
                                } ?: listOf()

                                amplitude.track("API_CALL_SUCCESSFUL")

                                val newEntityList = newsArticleList.map {
                                    NewsEntity(
                                        title = it.title,
                                        image = it.image,
                                        description = it.description,
                                        newsUrl = it.newsUrl
                                    )
                                }
                                sharedPreferences.edit().apply {
                                    putLong(Helpers.API_TIME, System.currentTimeMillis())
                                    apply()
                                }
                                newsRepo.insert(newEntityList)
                                setupPeriodicWorkRequestUseCase.invoke()
                                responseFlow.emit(newsArticleList)
                            } else {
                                Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                            }
                        }
                        if (job?.isActive == true) job?.cancel()
                    }
                }
            } catch (e: Exception) {
                Log.e("_ERROR", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun getCountryInfo(context: Context, location: Location) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val countryName = addresses[0].countryName
                val countryCode = addresses[0].countryCode
                Log.d("UserCountry", "Country: $countryName ($countryCode)")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


