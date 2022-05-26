package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.database.Database;

@RunWith(AndroidJUnit4.class)

public class MonthlyLeaderBoardFragmentTest {

    ActivityScenario activity;

    @Before
    public void init(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("a.b@admin.ch", "123456");
        new Database().addHealthPoint(FirebaseAuth.getInstance().getCurrentUser().getUid(), 10);
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

    @Test
    public void initiate(){
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

    }
    @Test
    public void viewProfileButtonTest(){
        onView(withId(R.id.top1)).check(matches(isDisplayed()));
        onView(withId(R.id.top1)).perform(click());
       // onView(withId(R.id.viewProfileNoFriend)).perform(click());

       // onView(withId(R.id.viewProfile)).check(matches(isDisplayed()));
    }
}
