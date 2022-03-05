package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
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
            Espresso.onView(withId(R.id.greetingMessage)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("Hello " + TEST_TEXT + "!")
                    )
            );
        }
    }
}
