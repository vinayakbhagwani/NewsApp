package com.vinayak.apps.cardstacksdemoapp

import com.vinayak.apps.cardstacksdemoapp.data.NewsRepository
import javax.inject.Inject

class SetupPeriodicWorkRequestUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke() = newsRepository.setPeriodicWorkRequest()
}