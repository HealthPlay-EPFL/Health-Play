package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GamesMenuTest {
    @Rule
    public ActivityScenarioRule<GamesMenu> testRule = new ActivityScenarioRule<>(GamesMenu.class);

    @Test
    public void planthuntDescriptionCorrectlyDisplays() {
        Espresso.onView(withId(R.id.planthuntThumbnail)).perform(click());
        Espresso.onView(withId(R.id.planthuntDescription)).check(ViewAssertions.matches(isDisplayed()));
    }

    /*@Test
    public void kneetagDescriptionCorrectlyDisplays() {
        Espresso.onView(withId(R.id.kneetagThumbnail)).perform(scrollTo(), click());
        Espresso.onView(withId(R.id.kneetagDescription)).check(ViewAssertions.matches(isDisplayed()));
    }*/

    @Test
    public void planthuntPlayCorrectlyLaunches() {
        Espresso.onView(withId(R.id.planthuntPlay)).perform(click());
        Espresso.onView(withId(R.id.plantButton)).check(ViewAssertions.matches(isDisplayed()));
    }

}

