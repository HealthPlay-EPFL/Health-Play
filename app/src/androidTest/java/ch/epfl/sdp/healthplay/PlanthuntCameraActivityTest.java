package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlanthuntCameraActivityTest {

    private static final String CAMERA_NAME = "com.android.camera2";

    @Rule
    public IntentsTestRule<PlanthuntCameraActivity> intentsRule = new IntentsTestRule<>(PlanthuntCameraActivity.class);

    @Rule
    public ActivityScenarioRule<PlanthuntCameraActivity> testRule = new ActivityScenarioRule<>(PlanthuntCameraActivity.class);


    @Test
    public void cameraIsCorrectlyLaunched(){
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage(CAMERA_NAME)).respondWith(result);

        Espresso.onView(withId(R.id.buttonCapture)).perform(click());

        //intended(toPackage("com.android.camera2"));

        //Espresso.onView(withId(R.id.plantButton)).check(matches(isDisplayed()));
    }

    @Test
    public void collectionIsCorrectlyLaunched(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntCameraActivity.class);

        Espresso.onView(withId(R.id.buttonCollection)).perform(click());

        Espresso.onView(withId(R.id.plantCollectionList)).check(matches(isDisplayed()));
    }

}