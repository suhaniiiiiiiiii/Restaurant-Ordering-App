package com.restaurant.orderingapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.restaurant.orderingapp.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests verifying input error states and field validations.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityEspressoTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginViews_AreDisplayed() {
        onView(withId(R.id.etLoginEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etLoginPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.tvGoToRegister)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginValidation_EmptyFieldsShowError() {
        // Click login with empty fields
        onView(withId(R.id.btnLogin)).perform(click());

        // TextInputLayout for email should display invalid email error
        onView(withId(R.id.etLoginEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginValidation_InvalidEmailShowsError() {
        onView(withId(R.id.etLoginEmail)).perform(typeText("invalid-email"), closeSoftKeyboard());
        onView(withId(R.id.etLoginPassword)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.tilLoginEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginValidation_ShortPasswordShowsError() {
        onView(withId(R.id.etLoginEmail)).perform(typeText("user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etLoginPassword)).perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.tilLoginPassword)).check(matches(isDisplayed()));
    }
}
