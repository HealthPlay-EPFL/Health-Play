package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.planthunt.PlanthuntCameraActivity;

@RunWith(AndroidJUnit4.class)
public class PlanthuntCameraActivityTest {

    private static final String CAMERA_NAME = "com.android.camera2";

    @Rule
    public IntentsTestRule<PlanthuntCameraActivity> intentsRule = new IntentsTestRule<>(PlanthuntCameraActivity.class);

    @Rule
    public ActivityScenarioRule<PlanthuntCameraActivity> testRule = new ActivityScenarioRule<>(PlanthuntCameraActivity.class);




    @Test()
    public void collectionIsCorrectlyLaunched(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntCameraActivity.class);

        Espresso.onView(withId(R.id.buttonCollection)).perform(click());

        Espresso.onView(withId(R.id.plantCollectionList)).check(matches(isDisplayed()));
    }

}