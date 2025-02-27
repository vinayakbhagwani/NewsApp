package com.vinayak.apps.cardstacksdemoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val image: String,
    val description: String,
    val newsUrl: String
)