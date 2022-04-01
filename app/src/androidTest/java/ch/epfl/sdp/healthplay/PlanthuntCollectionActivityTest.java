package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PlanthuntCollectionActivityTest {
    @Rule
    public ActivityScenarioRule<PlanthuntResultActivity> testRule = new ActivityScenarioRule<>(PlanthuntResultActivity.class);

    @Before
    public void before() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
    }

    @Test
    public void itemIsCorrectlyLaunched(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntCameraActivity.class);

        /*onData(allOf()).atPosition(0).
                onChildView(withId(R.id.plantCollectionList)).
                perform(click());

        Espresso.onView(withId(R.id.plantItemName)).check(matches(isDisplayed()));*/
    }

}