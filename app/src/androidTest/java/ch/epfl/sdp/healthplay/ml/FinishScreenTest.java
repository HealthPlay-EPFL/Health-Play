package ch.epfl.sdp.healthplay.ml;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import ch.epfl.sdp.healthplay.BarcodeInformationActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.kneetag.FinishScreen;

public class FinishScreenTest {


    @Before
    public void before() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@epfl.ch", "123456");
    }

    @Test
    public void restartTest() {
        ActivityScenario.launch(FinishScreen.class);
        Espresso.onView(withId(R.id.restart)).perform(click());
        Espresso.onView(withId(R.id.friends)).check(matches(isDisplayed()));

    }

    @Test
    public void quitTest() {
        ActivityScenario.launch(FinishScreen.class);
        Espresso.onView(withId(R.id.quit)).perform(click());
        Espresso.onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));

    }
}
