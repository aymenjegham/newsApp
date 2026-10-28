package com.angelstudio.newsapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.domain.model.NewsSettings
import com.angelstudio.newsapp.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {
    private val appContext = context.applicationContext
    private val preferences: SharedPreferences =
        appContext.getSharedPreferences(
            "${appContext.packageName}_preferences",
            Context.MODE_PRIVATE
        )
    private val mutableSettings = MutableStateFlow(readSettings())

    override val settings: StateFlow<NewsSettings> = mutableSettings

    override suspend fun setCategories(categories: Set<String>) {
        require(categories.isNotEmpty()) { "At least one category must remain selected." }
        preferences.edit()
            .putString(KEY_CATEGORIES, categories.joinToString(","))
            .putString(appContext.getString(R.string.categories_setting), categories.first())
            .apply()
        mutableSettings.value = mutableSettings.value.copy(categories = categories)
    }

    override suspend fun setCountry(country: String) {
        preferences.edit()
            .putString(appContext.getString(R.string.country_setting), country)
            .apply()
        mutableSettings.value = mutableSettings.value.copy(country = country)
    }

    override suspend fun setLanguage(language: String) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply()
        mutableSettings.value = mutableSettings.value.copy(language = language)
    }

    override suspend fun setAppearance(appearance: Appearance) {
        preferences.edit()
            .putString(KEY_APPEARANCE, appearance.name)
            .putBoolean(appContext.getString(R.string.theme_setting), appearance == Appearance.DARK)
            .apply()
        mutableSettings.value = mutableSettings.value.copy(appearance = appearance)
    }

    private fun readSettings(): NewsSettings {
        val legacyCategory = preferences.getString(
            appContext.getString(R.string.categories_setting),
            DEFAULT_CATEGORY
        ) ?: DEFAULT_CATEGORY
        val categories = preferences.getString(KEY_CATEGORIES, null)
            ?.split(',')
            ?.filter(String::isNotBlank)
            ?.toSet()
            ?.takeIf(Set<String>::isNotEmpty)
            ?: setOf(legacyCategory)
        val appearance = preferences.getString(KEY_APPEARANCE, null)
            ?.let { runCatching { Appearance.valueOf(it) }.getOrNull() }
            ?: if (preferences.getBoolean(appContext.getString(R.string.theme_setting), false)) {
                Appearance.DARK
            } else {
                Appearance.LIGHT
            }

        return NewsSettings(
            categories = categories,
            country = preferences.getString(appContext.getString(R.string.country_setting), "us")
                ?: "us",
            language = preferences.getString(KEY_LANGUAGE, "en") ?: "en",
            appearance = appearance
        )
    }

    private companion object {
        const val KEY_CATEGORIES = "news_selected_categories"
        const val KEY_LANGUAGE = "news_language"
        const val KEY_APPEARANCE = "news_appearance"
        const val DEFAULT_CATEGORY = "general"
    }
}
