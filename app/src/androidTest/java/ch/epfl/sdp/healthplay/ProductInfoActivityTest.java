package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProductInfoActivityTest {
    private static final String TEST_CODE = "737628064502";
    @Rule
    public ActivityScenarioRule<ProductInfoActivity> testRule = new ActivityScenarioRule<>(ProductInfoActivity.class);

    @Test
    public void intentContainsCorrectBarcodeWhenButtonPressed() {

        Intents.init();
        // Get the TextView and write the test name onto it
        ViewInteraction textView = Espresso.onView(withId(R.id.barcodeText));
        textView.perform(ViewActions.typeText(TEST_CODE));
        Espresso.closeSoftKeyboard();


        // Get the button and click on it
        ViewInteraction button = Espresso.onView(withId(R.id.findProductInfos));
        button.perform(ViewActions.click());

        // This line checks that the intent has the correct key and the correct test name
        Intents.intended(toPackage("ch.epfl.sdp.healthplay"));

        Intents.release();
    }
}
