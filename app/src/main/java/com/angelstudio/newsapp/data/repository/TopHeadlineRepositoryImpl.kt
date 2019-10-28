package com.angelstudio.newsapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.angelstudio.newsapp.data.db.TopHeadlineDao
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.network.TopHeadlineDataSource
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import java.util.*

class TopHeadlineRepositoryImpl(
    private val topHeadlineDataSource:TopHeadlineDataSource,
    private val  topHeadlineDao: TopHeadlineDao
) : TopHeadlineRepository {

    init {
        topHeadlineDataSource.downloadedTopHeadline.observeForever{newTopHeadline ->
            persistFetchedTopHeadline(newTopHeadline)

        }
    }

    override suspend fun getTopHeadline(): LiveData<List<Article>> {
        return withContext(Dispatchers.IO){

            initTopHeadlineData()
            return@withContext topHeadlineDao.getTopHeadline()

        }
    }

    override suspend fun fetch() {
        initTopHeadlineData()
    }



    private fun persistFetchedTopHeadline(newTopHeadline: TopHeadlineNewsResponse) {

        fun deleteOldTopHeadline() {
           topHeadlineDao.deleteOldTopHeadline()
        }
        GlobalScope.launch(Dispatchers.IO) {
            deleteOldTopHeadline()
            val topHeadlineList = newTopHeadline.articles

            topHeadlineDao.insert(topHeadlineList)
        }

    }


    private suspend fun initTopHeadlineData() {
            fetchTopHeadline()

    }

    private suspend fun fetchTopHeadline() {
        topHeadlineDataSource.fetchTopHeadline(
            "sports","us","100"
        )
    }


}