package ch.epfl.sdp.healthplay;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private static final String TEST_NAME = "TestName";
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void intentContainsCorrectTextWhenButtonPressed() {
        // Get the TextView and write the test name onto it
        ViewInteraction textView = Espresso.onView(withId(R.id.mainName));
        textView.perform(ViewActions.typeText(TEST_NAME));
        Espresso.closeSoftKeyboard();

        Intents.init();

        // Get the button and click on it
        ViewInteraction button = Espresso.onView(withId(R.id.mainGoButton));
        button.perform(ViewActions.click());

        // This line checks that the intent has the correct key and the correct test name
        Intents.intended(toPackage("ch.epfl.sdp.healthplay"));
        Intents.intended(hasExtra(MainActivity.EXTRA_MESSAGE, TEST_NAME));

        Intents.release();
    }
}
