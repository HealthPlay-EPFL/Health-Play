package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeScreenActivityTest {
    @Before
    public void init() throws InterruptedException {
        AuthUiActivityTest.signIn(SignedInFragmentTest.emailString, SignedInFragmentTest.password);
        ActivityScenario activityScenario = ActivityScenario.launch(HomeScreenActivity.class);
    }
    @Test()
    public void activityInitialization() {
        Espresso.onView(withId(R.id.fragmentContainerView)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
    }
}
