package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class SignedTest {
    public static String emailString = "HP@admin.ch";
    public static String password = "123456";
    @Before
    public void init() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
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
        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailString, password);
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        onView(withHint("Email")).perform(typeText(SignedInFragmentTest.emailString), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        onView(withHint("New password")).perform(typeText(SignedInFragmentTest.password));
        /*onView(withHint("Password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);*/

        onView(withText("Save")).perform(click());
        TimeUnit.SECONDS.sleep(1);

    }

    @After
    public void remove_account(){
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
