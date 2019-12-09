package com.angelstudio.newsapp.ui.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModel

class ArchiveViewModelFactory(private val topHeadlineRepository: TopHeadlineRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArchiveViewModel(topHeadlineRepository) as T
    }
}