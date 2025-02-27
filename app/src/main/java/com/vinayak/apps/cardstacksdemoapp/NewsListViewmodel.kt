package com.vinayak.apps.cardstacksdemoapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import com.vinayak.apps.cardstacksdemoapp.data.RetrofitInstance
import com.vinayak.apps.cardstacksdemoapp.data.local.NewsEntity
import com.vinayak.apps.cardstacksdemoapp.dto.NewsApiResponse
import com.vinayak.apps.cardstacksdemoapp.models.NewsArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewsListViewmodel @Inject constructor(val newsRepo: NewsRepository) : ViewModel() {

    val responseFlow = MutableStateFlow<List<NewsArticle>>(listOf())
    var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("API_ERROR", "Error occurred: ${throwable.localizedMessage}")
    }

    fun fetchNews() {
        job = viewModelScope.launch(Dispatchers.Main + exceptionHandler) {
            try {
                newsRepo.getAllNewsList().collect { list ->
                    if (list.isNotEmpty()) {
                        Log.e("DB_LOAD", "came here")
                        val newsArticleList = list.map {
                            NewsArticle(
                                title = it.title,
                                description = it.description,
                                image = it.image,
                                newsUrl = it.newsUrl
                            )
                        }
                        responseFlow.emit(newsArticleList)
                    } else {
                        Log.e("DB_LOAD", "came here to API")
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
                                val newEntityList = newsArticleList.map {
                                    NewsEntity(
                                        title = it.title,
                                        image = it.image,
                                        description = it.description,
                                        newsUrl = it.newsUrl
                                    )
                                }
                                newsRepo.insert(newEntityList)
                                responseFlow.emit(newsArticleList)
                            } else {
                                Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                            }
                        }
                    }
                    if (job?.isActive == true) job?.cancel()
                }
            } catch (e: Exception) {
                Log.e("_ERROR", "Exception: ${e.localizedMessage}")
            }
        }
    }
}


