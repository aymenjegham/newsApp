package com.angelstudio.newsapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse
import com.angelstudio.newsapp.internal.NoConnectivityException

class TopHeadlineDataSourceImpl(
    private val newsApiService: NewsApiService
) : TopHeadlineDataSource {
    private val _downloadedTopHeadline = MutableLiveData<TopHeadlineNewsResponse>()
    override val downloadedTopHeadline: LiveData<TopHeadlineNewsResponse>
        get() = _downloadedTopHeadline

    override suspend fun fetchTopHeadline(category: String, country: String,pagesize:String) {
        try {
            val fetchTopHeadline = newsApiService
                .getTopHeadlines(category, country,pagesize)
                .await()
            _downloadedTopHeadline.postValue(fetchTopHeadline)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }
}