package ch.epfl.sdp.healthplay;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.widget.Gallery;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditProfilePictureFragmentTest {
    ActivityScenario activity;

    @Before
    public void init(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.profileActivity);
            }
        });
        onView(withId(R.id.changeButton)).perform(click());
    }

    @Test
    public void initiate(){
        onView(withId(R.id.edit_profile_picture)).check(matches(isDisplayed()));
        onView(withId(R.id.change_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.exit_button)).check(matches(isDisplayed()));
    }

    /*@Test
    public void exit(){
        onView(withId(R.id.exit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.exit_button)).perform(click());
        onView(withId(R.id.changeButton)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void change_image(){
        onView(withId(R.id.change_button)).check(matches(isDisplayed()));
        onView(withId(R.id.change_button)).perform(click());
    }*/

    /*@Test
    public void save(){
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).perform(click());
    }*/
}
