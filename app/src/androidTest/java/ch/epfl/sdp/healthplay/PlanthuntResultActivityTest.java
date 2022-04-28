package ch.epfl.sdp.healthplay;


import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.planthunt.PlanthuntResultActivity;

@RunWith(AndroidJUnit4.class)
public class PlanthuntResultActivityTest {
    private static final String TEST_TEXT = "flowerName";

    @Rule
    public ActivityScenarioRule<PlanthuntResultActivity> testRule = new ActivityScenarioRule<>(PlanthuntResultActivity.class);

    @Test
    public void planthuntButtonCorrectlyDisplaysName() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntResultActivity.class);
        intent.putExtra("name", TEST_TEXT);

        Espresso.onView(withId(R.id.plantButton)).perform(click());

        /*Espresso.onView(withId(R.id.plantDescription)).check(
                ViewAssertions.matches(
                        ViewMatchers.withText(TEST_TEXT)
                )
        );*/
    }




}
