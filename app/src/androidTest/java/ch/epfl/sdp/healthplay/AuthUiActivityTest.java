package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.AuthUiActivity;
import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class AuthUiActivityTest {

    @Before
    public void init() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
        WelcomeScreenActivity.cache = new DataCache(InstrumentationRegistry.getInstrumentation().getContext());
        ActivityScenario home = ActivityScenario.launch(HomeScreenActivity.class);
        home.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
    }

    @Test
    public void authenticate() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(SignedInFragmentTest.emailString), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        onView(withHint("Password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        //FirebaseAuth.getInstance().signInWithEmailAndPassword(SignedInFragmentTest.emailString, SignedInFragmentTest.password);
        ActivityScenario sc2 = ActivityScenario.launch(HomeScreenActivity.class);
        sc2.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.sign_out)).perform(click());
    }

    @Test
    public void remove_account() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText("HPTest@admin.ch"), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(5);
        onView(withHint("New password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("Save")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        ActivityScenario sc2 = ActivityScenario.launch(HomeScreenActivity.class);
        sc2.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.delete_account)).perform(click());
        onView(withText("Yes, nuke it!")).perform(click());
    }

}
