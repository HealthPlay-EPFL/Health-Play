package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.model.Graph_Frag;

@RunWith(AndroidJUnit4.class)
public class GameMenuFragmentTest {
    @Before
    public void init() throws InterruptedException {
        FragmentScenario<GameMenuFragment> scenario = FragmentScenario.launchInContainer(GameMenuFragment.class);
        TimeUnit.SECONDS.sleep(1);
    }

    /*@Test
    public void planthuntDescriptionCorrectlyDisplays() {
        /*Espresso.onView(withId(R.id.planthuntThumbnail)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        Espresso.onView(withId(R.id.planthuntThumbnail)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.planthuntThumbnail)).perform(click());
        Espresso.onView(withId(R.id.planthuntDescription)).check(matches(isDisplayed()));
    }

    @Test
    public void kneetagDescriptionCorrectlyDisplays() throws InterruptedException {
        Espresso.onView(withId(R.id.kneetagThumbnail)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        Espresso.onView(withId(R.id.kneetagDescriptionF)).check(matches(isDisplayed()));
    }*/

    @Test
    public void planthuntPlayCorrectlyLaunches() {
        Espresso.onView(withId(R.id.planthuntPlay)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        Espresso.onView(withId(R.id.buttonCapture)).check(matches(isDisplayed()));
    }

}
