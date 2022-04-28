package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.healthplay.planthunt.PlanthuntCreateLobbyActivity;

public class PlanthuntCreateLobbyActivityTest {
    private static final String NAME = "test";
    private static final String PASSWORD = "password";


    @Rule
    public ActivityScenarioRule<PlanthuntCreateLobbyActivity> testRule = new ActivityScenarioRule<>(PlanthuntCreateLobbyActivity.class);

    @Test
    public void lobbyIsCorrectlyCreated() {
        ViewInteraction textName = Espresso.onView(withId(R.id.createLobbyName));
        textName.perform(ViewActions.typeText(NAME));
        Espresso.closeSoftKeyboard();
        ViewInteraction textPassword = Espresso.onView(withId(R.id.createLobbyPassword));
        textPassword.perform(ViewActions.typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.createLobbyButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click plus button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );

        Espresso.onView(withId(R.id.createLobbyButton)).check(matches(isDisplayed()));
    }
}