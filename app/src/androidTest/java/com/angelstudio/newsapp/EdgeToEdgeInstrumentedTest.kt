package com.angelstudio.newsapp

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EdgeToEdgeInstrumentedTest {

    @Test
    fun toolbarAndBottomNavigationStayInsideTheDeviceSafeArea() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.main_root)).check(matches(isDisplayed()))
            scenario.onActivity { activity ->
                val root = activity.findViewById<View>(R.id.main_root)
                val insets = ViewCompat.getRootWindowInsets(root)
                assertNotNull(insets)
                val safeArea = insets!!.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
                )
                assertPadding(root, safeArea.left, safeArea.top, safeArea.right, safeArea.bottom)

                val appBar = activity.findViewById<View>(R.id.appBarLayout)
                val bottomNav = activity.findViewById<View>(R.id.bottom_nav)
                assertTrue(
                    "App bar top ${appBar.top} must be below top inset ${safeArea.top}",
                    appBar.top >= safeArea.top
                )
                assertTrue(
                    "Bottom navigation bottom ${bottomNav.bottom} must be within ${root.height - safeArea.bottom}",
                    bottomNav.bottom <= root.height - safeArea.bottom
                )
            }
        }
    }

    @Test
    fun safeAreaInsetsAreAppliedOnceAndUpdatedWithoutAccumulating() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val root = activity.findViewById<View>(R.id.main_root)
                val safeAreaTypes = WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
                val keyboard = Insets.of(0, 0, 0, 300)
                val gestureInsets = WindowInsetsCompat.Builder()
                    .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(0, 24, 0, 24))
                    .setInsets(WindowInsetsCompat.Type.ime(), keyboard)
                    .build()

                val remaining = ViewCompat.dispatchApplyWindowInsets(root, gestureInsets)
                assertPadding(root, 0, 24, 0, 24)
                assertEquals(Insets.NONE, remaining.getInsets(safeAreaTypes))
                assertEquals(keyboard, remaining.getInsets(WindowInsetsCompat.Type.ime()))

                ViewCompat.dispatchApplyWindowInsets(root, gestureInsets)
                assertPadding(root, 0, 24, 0, 24)

                val cutoutInsets = WindowInsetsCompat.Builder()
                    .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(0, 24, 0, 48))
                    .setInsets(WindowInsetsCompat.Type.displayCutout(), Insets.of(32, 40, 16, 0))
                    .build()
                ViewCompat.dispatchApplyWindowInsets(root, cutoutInsets)
                assertPadding(root, 32, 40, 16, 48)

                ViewCompat.dispatchApplyWindowInsets(root, WindowInsetsCompat.CONSUMED)
                assertPadding(root, 0, 0, 0, 0)
            }
        }
    }

    private fun assertPadding(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        assertEquals(left, view.paddingLeft)
        assertEquals(top, view.paddingTop)
        assertEquals(right, view.paddingRight)
        assertEquals(bottom, view.paddingBottom)
    }
}
