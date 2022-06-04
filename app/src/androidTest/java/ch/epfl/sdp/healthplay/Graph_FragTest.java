package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.model.Graph_Frag;

@RunWith(AndroidJUnit4.class)
public class Graph_FragTest {

    @Before
    public void init() throws InterruptedException {
        AuthUiActivityTest.signIn("HP@admin.ch", "123456");
        ActivityScenario sc = ActivityScenario.launch(HomeScreenActivity.class);
        onView(withId(R.id.switchFragButton)).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void normalLaunchFrag() throws InterruptedException {
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
        AuthUiActivityTest.signOut();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void caloriesClickOnNext() throws InterruptedException {
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
        AuthUiActivityTest.signOut();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void caloriesClickOnPrev() throws InterruptedException {
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonPrev)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
        AuthUiActivityTest.signOut();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void caloriesClickOnNextAfterClickOnHealth() throws InterruptedException {
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isNotEnabled()));
        AuthUiActivityTest.signOut();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void clickOnCalories() throws InterruptedException {
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonCalories)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
        AuthUiActivityTest.signOut();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void clickOnHealthAfterClickOnNextAfterClickOnCalories() throws InterruptedException {
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonCalories)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
        AuthUiActivityTest.signOut();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void cache() throws InterruptedException {
        onView(withId(R.id.buttonSwap)).perform(click());
        onView( allOf( withId(R.id.SignedInFragment), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.sign_out)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.switchFragButton)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.buttonSwap)).perform(click());
    }
}
