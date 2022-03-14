package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.database.User;


@RunWith(AndroidJUnit4.class)

public class ProfileActivityTest {

    @Before
    public void initUser() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra(ProfileSetupActivity.EXTRA_USERNAME, "Hugo");
        intent.putExtra(ProfileSetupActivity.EXTRA_ID, "123");
        User.writeNewUser("123", "Hugo", 0, 0);
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent);
    }

    @Test
    public void TestgreetingMessage() {

        //try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
        Espresso.onView(withId(R.id.ProfileGreeting)).check(
                ViewAssertions.matches(
                        ViewMatchers.withText("Hi " + "Hugo")
                )
        );

        User.deleteUser("123");

        //}
    }

    @Test
    public void TestZeroCalorie() {

        //try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {

        Espresso.onView(withId(R.id.CalorieCounter)).check(
                ViewAssertions.matches(
                        ViewMatchers.withText("null calories!")
                )
        );
        User.deleteUser("123");
        //}
    }

     

}
