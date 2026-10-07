package com.angelstudio.newsapp

import androidx.preference.Preference
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.ui.MainActivity
import com.angelstudio.newsapp.ui.SettingsFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactPageInstrumentedTest {

    @Test
    fun settingsContactOpensPageAndPreservesBackNavigationAfterRecreation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.navController.navigate(R.id.settingsFragment)
            }
            onView(withId(R.id.categories_list)).check(matches(isDisplayed()))
            scenario.onActivity { activity ->
                val settings = activity.navHostFragment.childFragmentManager
                    .primaryNavigationFragment as SettingsFragment
                val contact = requireNotNull(settings.findPreference<Preference>("contact"))
                assertTrue(contact.isEnabled)
                assertEquals(activity.getString(R.string.jegham_aymen_89_gmail_com), contact.summary)
                contact.performClick()
            }
            onView(withId(R.id.contact_email_address))
                .check(matches(withText("jegham.aymen.89@gmail.com")))
            onView(withId(R.id.contact_email_button)).check(matches(isDisplayed()))
            scenario.recreate()
            onView(withId(R.id.contact_email_address)).check(matches(isDisplayed()))
            scenario.onActivity { activity ->
                assertEquals(R.id.contactFragment, activity.navController.currentDestination?.id)
                assertEquals(activity.getString(R.string.contact_us), activity.supportActionBar?.title)
                assertTrue(activity.navController.popBackStack())
            }
            onView(withId(R.id.categories_list)).check(matches(isDisplayed()))
            scenario.onActivity { activity ->
                assertEquals(R.id.settingsFragment, activity.navController.currentDestination?.id)
            }
        }
    }
}
