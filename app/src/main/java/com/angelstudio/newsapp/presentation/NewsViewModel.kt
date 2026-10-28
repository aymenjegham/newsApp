package com.angelstudio.newsapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.domain.model.NewsArticle
import com.angelstudio.newsapp.domain.model.NewsSettings
import com.angelstudio.newsapp.domain.model.SavedArticle
import com.angelstudio.newsapp.domain.usecase.ManageNewsSettingsUseCase
import com.angelstudio.newsapp.domain.usecase.ManageSavedArticlesUseCase
import com.angelstudio.newsapp.domain.usecase.ObserveHeadlinesUseCase
import com.angelstudio.newsapp.domain.usecase.ObserveSavedArticlesUseCase
import com.angelstudio.newsapp.domain.usecase.RefreshHeadlinesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val headlines: List<NewsArticle> = emptyList(),
    val savedArticles: List<SavedArticle> = emptyList(),
    val settings: NewsSettings = NewsSettings(
        categories = setOf("general"),
        country = "us",
        language = "en",
        appearance = Appearance.LIGHT
    ),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    observeHeadlines: ObserveHeadlinesUseCase,
    observeSavedArticles: ObserveSavedArticlesUseCase,
    private val refreshHeadlines: RefreshHeadlinesUseCase,
    private val manageSavedArticles: ManageSavedArticlesUseCase,
    private val manageNewsSettings: ManageNewsSettingsUseCase
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private var refreshJob: Job? = null

    val uiState: StateFlow<NewsUiState> = combine(
        observeHeadlines(),
        observeSavedArticles(),
        manageNewsSettings.settings,
        isRefreshing,
        errorMessage
    ) { headlines, saved, settings, refreshing, error ->
        NewsUiState(headlines, saved, settings, refreshing, error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NewsUiState()
    )

    init {
        refresh()
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            isRefreshing.value = true
            errorMessage.value = null
            val settings = manageNewsSettings.settings.value
            refreshHeadlines(settings.categories, settings.country)
                .onFailure { errorMessage.value = it.message }
            isRefreshing.value = false
        }
    }

    fun selectCategory(category: String) {
        viewModelScope.launch {
            manageNewsSettings.setCategories(setOf(category))
            refresh()
        }
    }

    fun toggleCategory(category: String) {
        viewModelScope.launch {
            val categories = manageNewsSettings.settings.value.categories
            val updated = if (category in categories) categories - category else categories + category
            if (updated.isNotEmpty()) {
                manageNewsSettings.setCategories(updated)
                refresh()
            }
        }
    }

    fun selectCountry(country: String) {
        viewModelScope.launch {
            manageNewsSettings.setCountry(country)
            refresh()
        }
    }

    fun selectLanguage(language: String) {
        viewModelScope.launch { manageNewsSettings.setLanguage(language) }
    }

    fun selectAppearance(appearance: Appearance) {
        viewModelScope.launch { manageNewsSettings.setAppearance(appearance) }
    }

    fun save(article: NewsArticle) {
        viewModelScope.launch { manageSavedArticles.save(article) }
    }

    fun removeSaved(article: NewsArticle) {
        viewModelScope.launch { manageSavedArticles.remove(article) }
    }

    fun markRead(article: NewsArticle) {
        article.url?.let { url ->
            viewModelScope.launch { manageSavedArticles.markRead(url) }
        }
    }

    fun deleteAllSaved() {
        viewModelScope.launch { manageSavedArticles.deleteAll() }
    }

    fun clearError() {
        errorMessage.value = null
    }
}
