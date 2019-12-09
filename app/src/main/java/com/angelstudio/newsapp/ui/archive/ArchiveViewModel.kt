package com.angelstudio.newsapp.ui.archive

import android.util.Log
import androidx.lifecycle.ViewModel
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.repository.TopHeadlineRepository
import com.angelstudio.newsapp.internal.lazyDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ArchiveViewModel(private val topHeadlineRepository: TopHeadlineRepository) : ViewModel() {

    val archivedArticles by lazyDeferred{
        topHeadlineRepository.getArchive()
    }

    fun desarchive(article: Article) {
        GlobalScope.launch(Dispatchers.IO) {
            topHeadlineRepository.desarchive(article)
        }
    }

    fun deleteAll() {
        GlobalScope.launch(Dispatchers.IO) {
            topHeadlineRepository.deleteAll()
        }
    }
}
