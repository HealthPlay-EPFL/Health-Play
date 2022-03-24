package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.AuthUiActivity;

@RunWith(AndroidJUnit4.class)
public class WelcomeScreenActivityTest {

    @Before
    public void init(){
        ActivityScenario activityTest = ActivityScenario.launch(WelcomeScreenActivity.class);
    }
    @Test
    public void startingNewIntentAuto() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.HomeView)).check(matches(isDisplayed()));
    }
    /*@Test
    public void startingNewIntentWithTouching() {
        onView(withId(R.id.my_view)).perform(ViewActions.click());
        onView(withId(R.id.main_view)).check(matches(isDisplayed()));
    }

    @Test
    public void pulseTest() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.logo_image)).check(matches(ViewMatchers.withAlpha(1f)));
    }*/

}
