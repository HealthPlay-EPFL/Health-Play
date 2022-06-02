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
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        ActivityScenario sc = ActivityScenario.launch(WelcomeScreenActivity.class);
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.switchFragButton)).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }
    @Test
    public void normalLaunchFrag() {
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void caloriesClickOnNext()  {
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void caloriesClickOnPrev(){
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonPrev)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void caloriesClickOnNextAfterClickOnHealth(){
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isNotEnabled()));
    }

    @Test
    public void clickOnCalories(){
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonCalories)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnHealthAfterClickOnNextAfterClickOnCalories(){
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonCalories)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
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
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
    }
}
