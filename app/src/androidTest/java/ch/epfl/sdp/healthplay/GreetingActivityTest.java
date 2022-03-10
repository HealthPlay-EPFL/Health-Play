package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {
    private static final String TEST_TEXT = "testText";

    @Test
    public void test() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, TEST_TEXT);

        try (ActivityScenario<GreetingActivity> scenario = ActivityScenario.launch(intent)) {

            ViewInteraction button = Espresso.onView(withId(R.id.incrCalories));
            button.perform(ViewActions.click());

            Espresso.onView(withId(R.id.CalorieCounter)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("1 " + "Calories")
                    )
            );
        }
    }
}
