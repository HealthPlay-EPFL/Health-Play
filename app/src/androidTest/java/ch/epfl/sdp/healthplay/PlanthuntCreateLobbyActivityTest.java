package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

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
        ViewInteraction buttonLobby = Espresso.onView(withId(R.id.createLobbyButton));
        buttonLobby.perform(ViewActions.click());

        Espresso.onView(withId(R.id.createLobbyButton)).check(matches(isDisplayed()));
    }
}