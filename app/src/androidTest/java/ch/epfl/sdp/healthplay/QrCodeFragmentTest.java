package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class QrCodeFragmentTest {
    @Before
    public void before() throws InterruptedException{
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        onView( allOf( withId(R.id.profileActivity), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.goToQRCode)).perform(click());
    }

    @Test
    public void backToProfile(){
        Espresso.onView(withId(R.id.backToProfileButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(click());
        onView(withId(R.id.statsButton)).check(matches(isDisplayed()));
    }

}