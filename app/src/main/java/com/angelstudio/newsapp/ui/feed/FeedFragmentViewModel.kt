package com.angelstudio.newsapp.ui.feed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository
import com.angelstudio.newsapp.internal.lazyDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedFragmentViewModel(private val topHeadlineRepository: TopHeadlineRepository) :ViewModel(){

    val topHeadline by lazyDeferred{
        topHeadlineRepository.getTopHeadline()
     }
    val isConnected by lazyDeferred{
        topHeadlineRepository.getConnectivity()
    }
    suspend fun fetchTopHeadline(){

        topHeadlineRepository.fetch()
    }



    private val _navigateToDetail = MutableLiveData<String>()
    val navigateToDetail by lazyDeferred {
        _navigateToDetail
    }
        //get() = _navigateToDetail


    fun onTopHeadlineClicked(url: String) {
        _navigateToDetail.value=url
    }

    fun onDetailNavigated() {
        _navigateToDetail.value=null
    }

       fun archive(article: Article) {
          GlobalScope.launch(Dispatchers.IO) {
              topHeadlineRepository.archive(article)
          }
    }


}