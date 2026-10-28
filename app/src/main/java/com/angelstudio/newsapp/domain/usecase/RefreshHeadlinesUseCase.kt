package com.angelstudio.newsapp.domain.usecase

import com.angelstudio.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class RefreshHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(categories: Set<String>, country: String) =
        repository.refresh(categories, country)
}
