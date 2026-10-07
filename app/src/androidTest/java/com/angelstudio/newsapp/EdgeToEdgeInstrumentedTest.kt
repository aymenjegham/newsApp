package com.angelstudio.newsapp

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.ui.MainActivity
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EdgeToEdgeInstrumentedTest {

    @Test
    fun settingsFlagsScrollFullyAboveBottomNavigation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.navController.navigate(R.id.settingsFragment)
            }
            onView(withId(androidx.preference.R.id.recycler_view)).perform(object : ViewAction {
                override fun getConstraints(): Matcher<View> = isAssignableFrom(RecyclerView::class.java)

                override fun getDescription() = "Scroll to the last preference"

                override fun perform(uiController: UiController, view: View) {
                    val list = view as RecyclerView
                    list.scrollBy(0, list.computeVerticalScrollRange())
                    uiController.loopMainThreadUntilIdle()
                }
            })
            for (flagId in listOf(R.id.imageView, R.id.imageView2, R.id.imageView3)) {
                onView(withId(flagId)).check(matches(isCompletelyDisplayed()))
                scenario.onActivity { activity ->
                    val flag = activity.findViewById<View>(flagId)
                    val bottomNav = activity.findViewById<View>(R.id.bottom_nav)
                    val flagLocation = IntArray(2)
                    val navLocation = IntArray(2)
                    flag.getLocationOnScreen(flagLocation)
                    bottomNav.getLocationOnScreen(navLocation)
                    assertTrue(
                        "Language flag must be fully above the bottom navigation",
                        flagLocation[1] + flag.height <= navLocation[1]
                    )
                }
            }
        }
    }

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
