package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Graph_FragTest {

    @Test
    public void normalLaunchFrag(){
        float[] weekDataC = {15,15,15,15,15,15,30};
        float[] weekDataH = {3,4,10,30,20,20,30};
        float[] monthDataC = {15,15,15,15,15,15,30,30,30,30,30,30};
        float[] monthDataH = {60,70,30,50,40,30,20,10,30,10,10,0};
        Bundle bundle = new Bundle();
        bundle.putFloatArray("weekC", weekDataC);
        bundle.putFloatArray("weekH", weekDataH);
        bundle.putFloatArray("monthC", monthDataC);
        bundle.putFloatArray("monthH", monthDataH);
        FragmentScenario<Graph_Frag> scenario = FragmentScenario.launchInContainer(Graph_Frag.class, bundle);
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void caloriesClickOnNext(){
        float[] weekDataC = {15,15,15,15,15,15,30};
        float[] weekDataH = {3,4,10,30,20,20,30};
        float[] monthDataC = {15,15,15,15,15,15,30,30,30,30,30,30};
        float[] monthDataH = {60,70,30,50,40,30,20,10,30,10,10,0};
        Bundle bundle = new Bundle();
        bundle.putFloatArray("weekC", weekDataC);
        bundle.putFloatArray("weekH", weekDataH);
        bundle.putFloatArray("monthC", monthDataC);
        bundle.putFloatArray("monthH", monthDataH);
        FragmentScenario<Graph_Frag> scenario = FragmentScenario.launchInContainer(Graph_Frag.class, bundle);
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void caloriesClickOnPrev(){
        float[] weekDataC = {15,15,15,15,15,15,30};
        float[] weekDataH = {3,4,10,30,20,20,30};
        float[] monthDataC = {15,15,15,15,15,15,30,30,30,30,30,30};
        float[] monthDataH = {60,70,30,50,40,30,20,10,30,10,10,0};
        Bundle bundle = new Bundle();
        bundle.putFloatArray("weekC", weekDataC);
        bundle.putFloatArray("weekH", weekDataH);
        bundle.putFloatArray("monthC", monthDataC);
        bundle.putFloatArray("monthH", monthDataH);
        FragmentScenario<Graph_Frag> scenario = FragmentScenario.launchInContainer(Graph_Frag.class, bundle);
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonPrev)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void caloriesClickOnNextAfterClickOnHealth(){
        float[] weekDataC = {15,15,15,15,15,15,30};
        float[] weekDataH = {3,4,10,30,20,20,30};
        float[] monthDataC = {15,15,15,15,15,15,30,30,30,30,30,30};
        float[] monthDataH = {60,70,30,50,40,30,20,10,30,10,10,0};
        Bundle bundle = new Bundle();
        bundle.putFloatArray("weekC", weekDataC);
        bundle.putFloatArray("weekH", weekDataH);
        bundle.putFloatArray("monthC", monthDataC);
        bundle.putFloatArray("monthH", monthDataH);
        FragmentScenario<Graph_Frag> scenario = FragmentScenario.launchInContainer(Graph_Frag.class, bundle);
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isNotEnabled()));
    }

    @Test
    public void clickOnCalories(){
        float[] weekDataC = {15,15,15,15,15,15,30};
        float[] weekDataH = {3,4,10,30,20,20,30};
        float[] monthDataC = {15,15,15,15,15,15,30,30,30,30,30,30};
        float[] monthDataH = {60,70,30,50,40,30,20,10,30,10,10,0};
        Bundle bundle = new Bundle();
        bundle.putFloatArray("weekC", weekDataC);
        bundle.putFloatArray("weekH", weekDataH);
        bundle.putFloatArray("monthC", monthDataC);
        bundle.putFloatArray("monthH", monthDataH);
        FragmentScenario<Graph_Frag> scenario = FragmentScenario.launchInContainer(Graph_Frag.class, bundle);
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonCalories)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnHealthAfterClickOnNextAfterClickOnCalories(){
        float[] weekDataC = {15,15,15,15,15,15,30};
        float[] weekDataH = {3,4,10,30,20,20,30};
        float[] monthDataC = {15,15,15,15,15,15,30,30,30,30,30,30};
        float[] monthDataH = {60,70,30,50,40,30,20,10,30,10,10,0};
        Bundle bundle = new Bundle();
        bundle.putFloatArray("weekC", weekDataC);
        bundle.putFloatArray("weekH", weekDataH);
        bundle.putFloatArray("monthC", monthDataC);
        bundle.putFloatArray("monthH", monthDataH);
        FragmentScenario<Graph_Frag> scenario = FragmentScenario.launchInContainer(Graph_Frag.class, bundle);
        onView(withId(R.id.buttonHealth)).perform(click());
        onView(withId(R.id.buttonNext)).perform(click());
        onView(withId(R.id.buttonCalories)).perform(click());
        onView(withId(R.id.buttonPrev)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonNext)).check(matches(isEnabled()));
        onView(withId(R.id.buttonCalories)).check(matches(isNotEnabled()));
        onView(withId(R.id.buttonHealth)).check(matches(isEnabled()));
    }
}
