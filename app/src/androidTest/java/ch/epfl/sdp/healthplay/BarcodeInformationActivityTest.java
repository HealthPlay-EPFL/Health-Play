package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BarcodeInformationActivityTest {
    private static final String TEST_CODE = "737628064502";
    private float calorieCount = 385;

    @Before
    public void before() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
    }

    @Test
    public void testInterface() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, TEST_CODE);

        try (ActivityScenario<BarcodeInformationActivity> ignored = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.pName)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("Thai peanut noodle kit includes stir-fry rice noodles & thai peanut seasoning")
                    )
            );
            Espresso.onView(withId(R.id.pEnergy)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("385")
                    )
            );
        }
    }


    @Test
    public void testUnknown() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, "TEST_CODE");
        try (ActivityScenario<BarcodeInformationActivity> ignored = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.pName)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("Unknown")
                    )
            );
            Espresso.onView(withId(R.id.pEnergy)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText("Unknown")
                    )
            );
        }

    }

    @Test
    public void checkUserNotNull() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, TEST_CODE);

        try (ActivityScenario<BarcodeInformationActivity> ignored = ActivityScenario.launch(intent)) {
            closeSoftKeyboard();
        }
    }

    @Test
    public void changeCalorieText() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, TEST_CODE);
        try (ActivityScenario<BarcodeInformationActivity> ignored = ActivityScenario.launch(intent)) {
            closeSoftKeyboard();
            ViewInteraction button = Espresso.onView(withId(R.id.incr_button));
            // Click increment
            button.perform(ViewActions.click());

            Espresso.onView(withId(R.id.pEnergy)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText(Float.toString(calorieCount * 2))
                    )
            );

            button = Espresso.onView(withId(R.id.incr_button2));
            // Click decrement
            button.check(matches(allOf( isEnabled(), isClickable()))).perform(
                    new ViewAction() {
                        @Override
                        public Matcher<View> getConstraints() {
                            return ViewMatchers.isEnabled(); // no constraints, they are checked above
                        }

                        @Override
                        public String getDescription() {
                            return "click plus button";
                        }

                        @Override
                        public void perform(UiController uiController, View view) {
                            view.performClick();
                        }
                    }
            );

            Espresso.onView(withId(R.id.pEnergy)).check(
                    ViewAssertions.matches(
                            ViewMatchers.withText(Float.toString(calorieCount))
                    )
            );
        }
    }

    @Test
    public void addToUser() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BarcodeInformationActivity.class);
        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, TEST_CODE);
        try (ActivityScenario<BarcodeInformationActivity> ignored = ActivityScenario.launch(intent)) {
            closeSoftKeyboard();
            ViewInteraction button = Espresso.onView(withId(R.id.add_to_counter_button));
            // Click increment
            button.check(matches(allOf( isEnabled(), isClickable()))).perform(
                    new ViewAction() {
                        @Override
                        public Matcher<View> getConstraints() {
                            return ViewMatchers.isEnabled(); // no constraints, they are checked above
                        }

                        @Override
                        public String getDescription() {
                            return "click plus button";
                        }

                        @Override
                        public void perform(UiController uiController, View view) {
                            view.performClick();
                        }
                    }
            );
        }
    }
}
