package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onData;
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
import android.widget.Button;

import androidx.navigation.Navigation;

import androidx.test.core.app.ActivityScenario;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.database.DataCache;
import ch.epfl.sdp.healthplay.database.Database;

@RunWith(AndroidJUnit4.class)

public class MonthlyLeaderBoardFragmentTest {

    ActivityScenario activity;

    @Before
    public void init() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("a.b@admin.ch", "123456");
        new Database().addHealthPoint("Z77qDyTWaeQ3ApvL4ImmDhrrMaG2", 100000);
        TimeUnit.SECONDS.sleep(2);
        new Database().addHealthPoint("z0WPZPfFGMTatM2pX30TT71TWyo2", 50000);
        TimeUnit.SECONDS.sleep(2);
        activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.gamesMenu);
            }
        });
        onView(withId(R.id.button3)).perform(click());
    }

    @After
    public void finish() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        new Database().addHealthPoint("Z77qDyTWaeQ3ApvL4ImmDhrrMaG2", -100000);
        TimeUnit.SECONDS.sleep(2);
        new Database().addHealthPoint("z0WPZPfFGMTatM2pX30TT71TWyo2", -50000);
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void initiate(){
        /*
        onView(withId(R.id.top1)).check(matches(isDisplayed()));
        onView(withId(R.id.top2)).check(matches(isDisplayed()));
        onView(withId(R.id.top3)).check(matches(isDisplayed()));
        onView(withId(R.id.top4)).check(matches(isDisplayed()));
        onView(withId(R.id.top5)).check(matches(isDisplayed()));
        onView(withId(R.id.topText1)).check(matches(isDisplayed()));
        onView(withId(R.id.topText2)).check(matches(isDisplayed()));
        onView(withId(R.id.topText3)).check(matches(isDisplayed()));
        onView(withId(R.id.topText4)).check(matches(isDisplayed()));
        onView(withId(R.id.topText5)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_picture1)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_picture2)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_picture3)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_picture4)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_picture5)).check(matches(isDisplayed()));
        onView(withId(R.id.todayButton)).check(matches(isDisplayed()));
        onView(withId(R.id.monthBackButton)).check(matches(isDisplayed()));
        */

    }
    @Test
    public void viewMyProfileButtonTest(){
        onView(withId(R.id.top1)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.top1)).check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
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
        onData(Matchers.anything()).atPosition(0).perform(click());
        onView(withId(R.id.profile_picture)).check(matches(isDisplayed()));
    }
    @Test
    public void viewProfileButtonTest(){
        onView(withId(R.id.top2)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.top2)).check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
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
        onData(Matchers.anything()).atPosition(0).perform(click());
        onView(withId(R.id.profile_picture)).check(matches(isDisplayed()));
    }

    /*@Test
    public void addFriendMonthlyLeaderBoardTest(){
        onView(withId(R.id.top2)).check(matches(isDisplayed()));
        //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        //onView(withId(R.id.addFriendLeaderBoard)).perform(click());
        //onView(withContentDescription(R.string.add_to_friendlist)).perform(click());

    }*/

    @Test
    public void returnTest() {
        onView(withId(R.id.top1)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.monthBackButton)).check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
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
        onView(withId(R.id.button3)).check(matches(isDisplayed()));

    }

    @Test
    public void todayTest() {
        //onView(withId(R.id.top1)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.todayButton)).check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
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

        //onView(withId(R.id.todayBackButton)).check(matches(isDisplayed()));
    }



}
