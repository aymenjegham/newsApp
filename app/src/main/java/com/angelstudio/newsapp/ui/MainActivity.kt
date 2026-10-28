package com.angelstudio.newsapp.ui

import android.os.Bundle
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.presentation.NewsNavigation
import com.angelstudio.newsapp.presentation.NewsViewModel
import com.angelstudio.newsapp.presentation.theme.NewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        applySavedPreferences()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            NewsTheme {
                NewsNavigation(viewModel)
            }
        }
    }

    private fun applySavedPreferences() {
        val preferences = getSharedPreferences(
            "${packageName}_preferences",
            MODE_PRIVATE
        )
        val appearance = preferences.getString("news_appearance", null)
            ?.let { runCatching { Appearance.valueOf(it) }.getOrNull() }
            ?: if (preferences.getBoolean(getString(R.string.theme_setting), false)) {
                Appearance.DARK
            } else {
                Appearance.LIGHT
            }
        AppCompatDelegate.setDefaultNightMode(
            when (appearance) {
                Appearance.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                Appearance.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Appearance.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        val language = preferences.getString("news_language", "en") ?: "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    }
}