package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.action.ViewActions.click;
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

import ch.epfl.sdp.healthplay.R;

public class PlanthuntCreateLobbyActivityTest {
    private static final String NAME = "test";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "a";


    @Rule
    public ActivityScenarioRule<PlanthuntCreateLobbyActivity> testRule = new ActivityScenarioRule<>(PlanthuntCreateLobbyActivity.class);

    @Test
    public void lobbyIsCorrectlyCreated() {
        ViewInteraction textName = Espresso.onView(ViewMatchers.withId(R.id.planthuntCreateLobbyName));
        textName.perform(ViewActions.typeText(NAME));
        Espresso.closeSoftKeyboard();
        ViewInteraction textPassword = Espresso.onView(withId(R.id.planthuntCreateLobbyPassword));
        textPassword.perform(ViewActions.typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        ViewInteraction textUsername = Espresso.onView(withId(R.id.planthuntCreateLobbyUsername));
        textUsername.perform(ViewActions.typeText(USERNAME));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.planthuntCreateLobbyButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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

        Espresso.onView(withId(R.id.planthuntCreateLobbyButton)).check(matches(isDisplayed()));
    }

    @Test
    public void checkBox1CorrectlyAppears() {
        Espresso.onView(withId(R.id.planthuntCreateLobbyBox1)).perform(click());
        Espresso.onView(withId(R.id.planthuntCreateLobbyBox1)).check(matches(isDisplayed()));
    }

    @Test
    public void checkBox2CorrectlyAppears() {
        Espresso.onView(withId(R.id.planthuntCreateLobbyBox2)).perform(click());
        Espresso.onView(withId(R.id.planthuntCreateLobbyBox2)).check(matches(isDisplayed()));
    }

    @Test
    public void checkBox3CorrectlyAppears() {
        Espresso.onView(withId(R.id.planthuntCreateLobbyBox3)).perform(click());
        Espresso.onView(withId(R.id.planthuntCreateLobbyBox3)).check(matches(isDisplayed()));
    }
}