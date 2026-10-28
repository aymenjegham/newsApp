package com.angelstudio.newsapp.domain.usecase

import com.angelstudio.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class ObserveSavedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke() = repository.savedArticles
}
