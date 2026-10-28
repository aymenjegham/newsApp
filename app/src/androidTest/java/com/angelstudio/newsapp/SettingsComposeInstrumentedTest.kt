package com.angelstudio.newsapp

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.domain.model.Appearance
import com.angelstudio.newsapp.domain.model.NewsSettings
import com.angelstudio.newsapp.presentation.SettingsScreen
import com.angelstudio.newsapp.presentation.theme.NewsTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsComposeInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsCategorySelectionIsDeliveredToViewModelBoundary() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var selectedCategory: String? = null
        composeRule.setContent {
            NewsTheme {
                SettingsScreen(
                    settings = NewsSettings(
                        categories = setOf("general"),
                        country = "us",
                        language = "en",
                        appearance = Appearance.LIGHT
                    ),
                    onToggleCategory = { selectedCategory = it },
                    onSelectCountry = {},
                    onSelectLanguage = {},
                    onSelectAppearance = {},
                    onContact = {}
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.technology))
            .performScrollTo()
            .performClick()
        composeRule.runOnIdle { assertEquals("technology", selectedCategory) }
    }
}
