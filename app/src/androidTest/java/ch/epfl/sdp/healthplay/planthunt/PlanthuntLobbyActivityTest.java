package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.healthplay.R;

public class PlanthuntLobbyActivityTest {

    @Rule
    public ActivityScenarioRule<PlanthuntLobbyActivity> testRule = new ActivityScenarioRule<>(PlanthuntLobbyActivity.class);

    /*@Test
    public void timerCorrectlyAppears() {
        Espresso.onView(withId(R.id.planthuntLobbyTimeText)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void cameraCorrectlyLaunches() {
        Espresso.onView(withId(R.id.planthuntLobbyButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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

        //Espresso.onView(withId(R.id.planthuntCreateLobbyMain)).check(matches(isDisplayed()));
    }*/

}