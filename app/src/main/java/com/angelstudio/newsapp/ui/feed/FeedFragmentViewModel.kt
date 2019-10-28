package com.angelstudio.newsapp.ui.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository
import com.angelstudio.newsapp.internal.lazyDeferred

class FeedFragmentViewModel(
    private val topHeadlineRepository: TopHeadlineRepository
) :ViewModel(){


    val topHeadline by lazyDeferred{
        topHeadlineRepository.getTopHeadline()
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

}