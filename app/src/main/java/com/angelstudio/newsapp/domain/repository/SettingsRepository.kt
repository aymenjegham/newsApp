package com.angelstudio.newsapp.domain.repository

import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.domain.model.NewsSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<NewsSettings>

    suspend fun setCategories(categories: Set<String>)
    suspend fun setCountry(country: String)
    suspend fun setLanguage(language: String)
    suspend fun setAppearance(appearance: Appearance)
}
