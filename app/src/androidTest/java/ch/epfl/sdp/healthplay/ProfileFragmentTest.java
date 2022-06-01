package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {
    @Before
    public void init(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        onView( allOf( withId(R.id.profileActivity), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
    }

    @Test
    public void initiate(){
        onView(withId(R.id.profile_picture)).check(matches(isDisplayed()));
        onView(withId(R.id.profileUsername)).check(matches(isDisplayed()));
        onView(withId(R.id.statsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.changeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.profileName)).check(matches(isDisplayed()));
        onView(withId(R.id.profileBirthday)).check(matches(isDisplayed()));
        onView(withId(R.id.profileWeight)).check(matches(isDisplayed()));
        onView(withId(R.id.profileHealthPoint)).check(matches(isDisplayed()));
        onView(withId(R.id.goToQRCode)).check(matches(isDisplayed()));
    }

    @Test
    public void settingButton(){
        onView(withId(R.id.statsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.statsButton)).perform(click());
        onView(withId(R.id.modifyNameText)).check(matches(isDisplayed()));
    }

    @Test
    public void imageSettingButton(){
        onView(withId(R.id.changeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.changeButton)).perform(click());
        onView(withId(R.id.edit_profile_picture)).check(matches(isDisplayed()));
    }

    @Test
    public void offlineMode() throws InterruptedException {
        onView( allOf( withId(R.id.SignedInFragment), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.sign_out)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        onView( allOf( withId(R.id.profileActivity), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.profileUsername)).check(matches(isDisplayed()));
        onView( allOf( withId(R.id.SignedInFragment), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(SignedInFragmentTest.emailString), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withHint("Password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }

}
