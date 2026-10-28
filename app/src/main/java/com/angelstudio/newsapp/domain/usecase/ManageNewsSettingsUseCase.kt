package com.angelstudio.newsapp.domain.usecase

import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.domain.repository.SettingsRepository
import javax.inject.Inject

class ManageNewsSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    val settings = repository.settings

    suspend fun setCategories(categories: Set<String>) = repository.setCategories(categories)
    suspend fun setCountry(country: String) = repository.setCountry(country)
    suspend fun setLanguage(language: String) = repository.setLanguage(language)
    suspend fun setAppearance(appearance: Appearance) = repository.setAppearance(appearance)
}
