package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.auth.SignedInFragment;

//@RunWith(AndroidJUnit4.class)
public class SignInFragmentTest {

    /*@Before
    public void init(){
        FragmentScenario activityTest = FragmentScenario.launch(SignedInFragment.class);
    }

    @Test
    public void startingNewIntentAuto() {
        onView(withId(R.id.sign_out)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_account)).check(matches(isDisplayed()));
        onView(withId(R.id.light)).check(matches(isDisplayed()));
        onView(withId(R.id.night)).check(matches(isDisplayed()));

    }*/
}