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
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class KneetagDescriptionFragmentTest {
    @Before
    public void init(){
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        WelcomeScreenActivity.cache = new DataCache(InstrumentationRegistry.getInstrumentation().getContext());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.gamesMenu);
            }
        });
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