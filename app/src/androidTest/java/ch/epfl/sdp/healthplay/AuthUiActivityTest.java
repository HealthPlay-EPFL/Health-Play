package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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

    public static String emailTest = "HPTest@admin.ch";

    @Before
    public void init() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
        ActivityScenario home = ActivityScenario.launch(AuthUiActivity.class);
    }

    @Test
    public void authenticate() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(SignedInFragmentTest.emailString), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withHint("Password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        ActivityScenario home = ActivityScenario.launch(HomeScreenActivity.class);
        home.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.sign_out)).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void remove_account() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(emailTest), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(1);
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
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void alreadySignIn() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(SignedInFragmentTest.emailString), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withHint("Password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        ActivityScenario home = ActivityScenario.launch(AuthUiActivity.class);
        TimeUnit.SECONDS.sleep(1);
    }

    public static void signIn(String email, String password) throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        ActivityScenario.launch(HomeScreenActivity.class);
        onView( allOf( withId(R.id.SignedInFragment), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(email), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withHint("Password")).perform(typeText(password), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }

    public static void signOut() throws InterruptedException {
        ActivityScenario.launch(HomeScreenActivity.class);
        onView( allOf( withId(R.id.SignedInFragment), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.sign_out)).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }

}
