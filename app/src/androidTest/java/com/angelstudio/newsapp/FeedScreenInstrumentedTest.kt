package com.angelstudio.newsapp

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.presentation.FeedScreen
import com.angelstudio.newsapp.presentation.ArticleDetailScreen
import com.angelstudio.newsapp.presentation.ArticleRoute
import com.angelstudio.newsapp.presentation.NewsUiState
import com.angelstudio.newsapp.presentation.theme.NewsTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedScreenInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun feedRendersEmptyStateAndSendsCategorySelection() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var selectedCategory: String? = null

        composeRule.setContent {
            NewsTheme {
                FeedScreen(
                    state = NewsUiState(),
                    onRefresh = {},
                    onCategorySelected = { selectedCategory = it },
                    onSave = {},
                    onRemove = {},
                    onShare = {},
                    onOpenArticle = {}
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.no_headlines)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.general)).performClick()
        composeRule.runOnIdle { assertEquals("general", selectedCategory) }
    }

    @Test
    fun articleDetailsRenderInComposeAndOfferOriginalPublisherLink() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        composeRule.setContent {
            NewsTheme {
                ArticleDetailScreen(
                    route = ArticleRoute(
                        url = "https://example.com/article",
                        title = "Compose-only article detail",
                        source = "The Daily",
                        description = "Article summary",
                        content = "Article preview",
                        imageUrl = null,
                        publishedAt = null
                    ),
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Compose-only article detail").assertIsDisplayed()
        composeRule.onNodeWithText("Article summary").assertIsDisplayed()
        composeRule.onNodeWithText(
            context.getString(R.string.open_article_in_browser)
        ).assertIsDisplayed()
    }
}
