package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KneetagDescriptionFragmentTest {
    @Before
    public void init(){
        ActivityScenario activity = ActivityScenario.launch(WelcomeScreenActivity.class);
        onView( allOf( withId(R.id.gamesMenu), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.kneetagThumbnail)).perform(click());
    }

    @Test
    public void initiate(){
        onView(withId(R.id.kneetagDescButtonBack)).check(matches(isDisplayed()));
        onView(withId(R.id.kneetagDescButtonBack)).perform(click());
        onView(withId(R.id.planthuntPlay)).check(matches(isDisplayed()));
        onView(withId(R.id.planthuntThumbnail)).check(matches(isDisplayed()));
        onView(withId(R.id.kneetagThumbnail)).check(matches(isDisplayed()));
        onView(withId(R.id.kneetagPlay)).check(matches(isDisplayed()));
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
    }
}