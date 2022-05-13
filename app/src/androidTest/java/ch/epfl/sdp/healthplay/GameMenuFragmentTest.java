package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;

import android.view.View;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GameMenuFragmentTest {

    @Before
    public void init(){
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.gamesMenu);
            }
        });
    }

    /*@Test
    public void planthuntDescriptionCorrectlyDisplays() {
        onView(withId(R.id.planthuntThumbnail)).check(matches(isDisplayed()));
        onView(withId(R.id.planthuntThumbnail)).perform(click());
        onView(withId(R.id.plantDescButtonBack)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void kneetagDescriptionCorrectlyDisplays() {
        onView(withId(R.id.kneetagThumbnail)).check(matches(isDisplayed()));
        onView(withId(R.id.kneetagThumbnail)).perform(click());
        onView(withId(R.id.kneetagDescButtonBack)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void planthuntPlayCorrectlyLaunches() {
        onView(withId(R.id.planthuntPlay)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        onView(withId(R.id.planthuntMainPlay)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void kneetagPlayCorrectlyLaunches() {
        onView(withId(R.id.kneetagPlay)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        //onView(withId(R.id.planthuntMainPlay)).check(matches(isDisplayed()));
    }*/

    @Test
    public void viewIsDisplayed() {
        onView(withId(R.id.planthuntPlay)).check(matches(isDisplayed()));
        onView(withId(R.id.planthuntThumbnail)).check(matches(isDisplayed()));
        onView(withId(R.id.kneetagThumbnail)).check(matches(isDisplayed()));
        onView(withId(R.id.kneetagPlay)).check(matches(isDisplayed()));
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
    }

    @Test
    public void leaderBoardIsDisplayed(){
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
        onView(withId(R.id.button3)).perform(click());
        onView(withId(R.id.profile_picture1)).check(matches(isDisplayed()));
    }

}

