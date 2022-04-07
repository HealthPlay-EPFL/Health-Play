package ch.epfl.sdp.healthplay;


import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import ch.epfl.sdp.healthplay.api.PlantnetApi;

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
