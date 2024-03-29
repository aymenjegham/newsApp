package com.angelstudio.newsapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.angelstudio.newsapp.R

import com.angelstudio.newsapp.data.db.TopHeadlineDao
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.network.TopHeadlineDataSource
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TopHeadlineRepositoryImpl(
    context: Context,
    private val topHeadlineDataSource:TopHeadlineDataSource,
    private val  topHeadlineDao: TopHeadlineDao
) : TopHeadlineRepository {
    private val appContext =context.applicationContext

    private val preferences: SharedPreferences
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(appContext)

    init {
        topHeadlineDataSource.downloadedTopHeadline.observeForever{newTopHeadline ->
            persistFetchedTopHeadline(newTopHeadline)

        }
        topHeadlineDataSource.connectivityState.observeForever { state ->
            Log.v("checkingconnectivity",state.toString())
        }

    }


    override suspend fun getTopHeadline(): LiveData<List<Article>> {
        return withContext(Dispatchers.IO){

            initTopHeadlineData(appContext)
            return@withContext topHeadlineDao.getTopHeadline()


        }
    }

    override suspend fun getArchive(): LiveData<List<Article>> {
        return withContext(Dispatchers.IO){


            return@withContext topHeadlineDao.getArchive()


        }
    }

    override suspend fun getConnectivity(): LiveData<Boolean> {
        return topHeadlineDataSource.connectivityState
    }

    override suspend fun archive(article: Article) {
        topHeadlineDao.archive(
            Archive(
                        article.id,
                        article.author,
                        article.content,
                        article.description,
                        article.publishedAt,
                        article.source,
                        article.title,
                        article.url,
                        article.urlToImage
                    ))
    }



    override suspend fun desarchive(article: Article) {
        topHeadlineDao.desarchive(
                        Archive(
                        article.id,
                        article.author,
                        article.content,
                        article.description,
                        article.publishedAt,
                        article.source,
                        article.title,
                        article.url,
                        article.urlToImage
        ))
    }

    override suspend fun deleteAll() {
        topHeadlineDao.deleteAll()
    }


    override suspend fun fetch() {
        initTopHeadlineData(appContext)
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


    private suspend fun initTopHeadlineData(appContext: Context) {
            fetchTopHeadline(appContext)

    }




    private suspend fun fetchTopHeadline(appContext: Context) {
        val selectedCategory =preferences.getString(appContext.getString(R.string.categories_setting),"general")
        val selectedCountry =preferences.getString(appContext.getString(R.string.country_setting),"us")
        topHeadlineDataSource.fetchTopHeadline(

            selectedCategory!!,selectedCountry!!,"100"
        )
    }





}