package com.angelstudio.newsapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.newsapp.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactComposeInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun contactDestinationPreservesContentAcrossRecreationAndReturnsToSettings() {
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.Settings)).performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.contact_us))
            .performScrollTo()
            .performClick()
        assertContactContent()

        composeRule.activityRule.scenario.recreate()
        assertContactContent()

        composeRule.onNodeWithContentDescription(composeRule.activity.getString(R.string.back))
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.contact_us))
            .performScrollTo()
            .assertIsDisplayed()
    }

    private fun assertContactContent() {
        listOf(
            R.string.aymen_jegham,
            R.string.contact_description,
            R.string.jegham_aymen_89_gmail_com,
            R.string.contact_email_action,
            R.string.contact_details,
            R.string.contact_support_note,
            R.string.contact_website,
            R.string.contact_website_url
        ).forEach { resource ->
            composeRule.onNodeWithText(composeRule.activity.getString(resource))
                .performScrollTo()
                .assertIsDisplayed()
        }
    }
}
