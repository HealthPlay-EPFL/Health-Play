package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

//import ch.epfl.sdp.healthplay.auth.SignedInActivity;

//@RunWith(AndroidJUnit4.class)
public class SigninActivityTest {

    /*@Before
    public void init(){
        ActivityScenario activityTest = ActivityScenario.launch(SignedInActivity.class);
    }

    @Test
    public void startingNewIntentAuto() {
        onView(withId(R.id.sign_out)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_account)).check(matches(isDisplayed()));
        onView(withId(R.id.user_profile_picture)).check(matches(isDisplayed()));
        onView(withId(R.id.light)).check(matches(isDisplayed()));
        onView(withId(R.id.night)).check(matches(isDisplayed()));

    }*/
}
