package com.example.storysubmissionapp.view.welcome

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.utils.EspressoIdlingResource
import com.example.storysubmissionapp.view.login.LoginActivity
import com.example.storysubmissionapp.view.main.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class WelcomeActivityTest {

    private val dummyEmail = "testing@wahyu.com"
    private val dummyPass = "12345678"

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(WelcomeActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun fromWelcomeUntilLogin() {
        Thread.sleep(3000)
        //check if login button is displayed
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))

        //click login button (WelcomeActivity)
        onView(withId(R.id.loginButton)).perform(click())

        //check if already in LoginActivity
        Intents.intended(hasComponent(LoginActivity::class.java.name))

        //fill the form
        onView(withId(R.id.emailEditText)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(dummyPass), closeSoftKeyboard())

        //click login button (LoginActivity)
        onView(withId(R.id.loginButton)).perform(click())

        //check if already in MainActivity
        Intents.intended(hasComponent(MainActivity::class.java.name))

        //check if recyclerview is displayed
        onView(withId(R.id.rvStory)).check(matches(isDisplayed()))
    }


    @Test
    fun fromMainUntilLogout() {
        Thread.sleep(3000)
        Intents.intended(hasComponent(MainActivity::class.java.name))
        onView(withText(R.string.logout)).perform(click())
        Intents.intended(hasComponent(WelcomeActivity::class.java.name))
    }

}
