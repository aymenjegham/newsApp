package com.angelstudio.newsapp.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository

class FeedFragmentViewModelFactory(private val topHeadlineRepository: TopHeadlineRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FeedFragmentViewModel(topHeadlineRepository) as T
    }
}