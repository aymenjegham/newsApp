package com.angelstudio.newsapp.data.network

import androidx.lifecycle.LiveData
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse

interface TopHeadlineDataSource {
    val downloadedTopHeadline: LiveData<TopHeadlineNewsResponse>
    val connectivityState: LiveData<Boolean>


    suspend fun fetchTopHeadline(
        category:String,
        country:String,
        pagesize : String
    )

}