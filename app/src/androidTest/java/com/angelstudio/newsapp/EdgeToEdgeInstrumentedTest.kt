package com.angelstudio.newsapp

import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EdgeToEdgeInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun keepScreenAwake() {
        composeRule.runOnIdle {
            composeRule.activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    @Test
    fun navigationAndFeedRemainVisibleAfterActivityLaunch() {
        val root = composeRule.onRoot().fetchSemanticsNode().boundsInWindow
        composeRule.runOnIdle {
            assertEquals(0f, root.top, 1f)
            assertEquals(composeRule.activity.window.decorView.height.toFloat(), root.bottom, 1f)
        }
        assertInsideSafeArea(composeRule.onNodeWithText(composeRule.activity.getString(R.string.app_name)))
        assertInsideSafeArea(composeRule.onNodeWithText(composeRule.activity.getString(R.string.home)))
        assertInsideSafeArea(composeRule.onNodeWithText(composeRule.activity.getString(R.string.saved)))
        assertInsideSafeArea(composeRule.onNodeWithText(composeRule.activity.getString(R.string.Settings)))
    }

    @Test
    fun savedHeaderAndNavigationRemainInsideSystemBars() {
        val savedLabel = composeRule.activity.getString(R.string.saved)
        val savedTab = composeRule.onNodeWithText(savedLabel)
        assertInsideSafeArea(savedTab)
        savedTab.performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(savedLabel)
                .fetchSemanticsNodes(atLeastOneRootRequired = false).size == 2
        }
        val savedNodes = composeRule.onAllNodesWithText(savedLabel)
        assertInsideSafeArea(savedNodes[0])
        assertInsideSafeArea(savedNodes[1])
    }

    @Test
    fun settingsContentScrollsWithinTheNavigationShell() {
        val settings = composeRule.onNodeWithText(composeRule.activity.getString(R.string.Settings))
        assertInsideSafeArea(settings)
        settings.performClick()
        assertInsideSafeArea(
            composeRule.onNodeWithText(
                composeRule.activity.getString(R.string.settings_personalize)
            )
        )
        val contact =
            composeRule.onNodeWithText(composeRule.activity.getString(R.string.contact_us))
        contact.performScrollTo()
        composeRule.onRoot().performTouchInput {
            swipeUp(startY = height * 0.6f, endY = height * 0.2f)
        }
        assertInsideSafeArea(contact)
        val refreshError = composeRule.activity.getString(R.string.refresh_failed)
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasText(refreshError))
                .fetchSemanticsNodes(atLeastOneRootRequired = false).isEmpty()
        }
        assertInsideSafeArea(contact)
        contact.performClick()
        assertInsideSafeArea(
            composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.back))
        )
        val website = composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.contact_website_url)
        )
        website.performScrollTo()
        composeRule.onRoot().performTouchInput {
            swipeUp(startY = height * 0.6f, endY = height * 0.2f)
        }
        assertInsideSafeArea(website)
    }

    @Test
    fun searchAndNavigationStayAboveTheKeyboard() {
        val searchButton = composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.show_search)
        )
        assertInsideSafeArea(searchButton)
        searchButton.performClick()
        val search = composeRule.onNode(hasSetTextAction())
        search.performClick().performTextInput("headlines")
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.runOnIdle {
                ViewCompat.getRootWindowInsets(composeRule.activity.window.decorView)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            }
        }
        val home = composeRule.onNodeWithText(composeRule.activity.getString(R.string.home))
        composeRule.waitUntil(timeoutMillis = 5_000) {
            val bottom = home.fetchSemanticsNode().boundsInWindow.bottom
            composeRule.runOnIdle {
                val decor = composeRule.activity.window.decorView
                val ime = checkNotNull(ViewCompat.getRootWindowInsets(decor))
                    .getInsets(WindowInsetsCompat.Type.ime())
                bottom <= decor.height - ime.bottom + 1
            }
        }
        assertInsideSafeArea(search, includeIme = true)
        assertInsideSafeArea(home, includeIme = true)
    }

    @Test
    fun systemBarIconsMatchTheActivityThemeWithoutNavigationScrim() {
        for (mode in listOf(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES)) {
            composeRule.activityRule.scenario.onActivity { it.delegate.localNightMode = mode }
            val expectedDark = mode == AppCompatDelegate.MODE_NIGHT_YES
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.runOnIdle {
                    val nightMode = composeRule.activity.resources.configuration.uiMode and
                            Configuration.UI_MODE_NIGHT_MASK
                    (nightMode == Configuration.UI_MODE_NIGHT_YES) == expectedDark
                }
            }
            composeRule.runOnIdle {
                val activity = composeRule.activity
                val controller =
                    WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                assertEquals(!expectedDark, controller.isAppearanceLightStatusBars)
                assertEquals(!expectedDark, controller.isAppearanceLightNavigationBars)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    assertFalse(activity.window.isNavigationBarContrastEnforced)
                }
            }
        }
    }

    private fun assertInsideSafeArea(
        node: SemanticsNodeInteraction,
        includeIme: Boolean = false
    ) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(isRoot())
                .fetchSemanticsNodes(atLeastOneRootRequired = false).isNotEmpty() &&
                    node.isDisplayed()
        }
        node.assertIsDisplayed()
        val bounds = node.fetchSemanticsNode().boundsInWindow
        val safeBounds = composeRule.runOnIdle {
            val decor = composeRule.activity.window.decorView
            val types = WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    if (includeIme) WindowInsetsCompat.Type.ime() else 0
            val insets = checkNotNull(ViewCompat.getRootWindowInsets(decor)).getInsets(types)
            Rect(
                insets.left.toFloat(),
                insets.top.toFloat(),
                (decor.width - insets.right).toFloat(),
                (decor.height - insets.bottom).toFloat()
            )
        }
        assertTrue(
            "Node $bounds must fit inside $safeBounds",
            bounds.left >= safeBounds.left - 1 &&
                    bounds.top >= safeBounds.top - 1 &&
                    bounds.right <= safeBounds.right + 1 &&
                    bounds.bottom <= safeBounds.bottom + 1
        )
    }
}
