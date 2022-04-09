package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorPrivacyManager;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class BarcodeScanActivityTest {

    @Rule
    public ActivityScenarioRule<BarcodeScanActivity> testRule =
            new ActivityScenarioRule<>(BarcodeScanActivity.class);

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(Manifest.permission.CAMERA,
                    Manifest.permission_group.CAMERA);

    @Before
    public void setup() {
        Intents.init();
        IdlingRegistry.getInstance().register(BarcodeScanActivity.idlingResource);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @After
    public void finish() {
        Intents.release();
        IdlingRegistry.getInstance().unregister(BarcodeScanActivity.idlingResource);
    }

    @Test
    public void testOnClick() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        ViewInteraction button = Espresso.onView(ViewMatchers.withId(R.id.get_information_from_barcode));
        button.check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
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

        TimeUnit.SECONDS.sleep(1);
        // Check if the intent is the correct destination
        //Intents.intended(IntentMatchers.hasExtra(BarcodeInformationActivity.EXTRA_MESSAGE, "7613356135901"));

    }

    @Test
    public void testEnterManually() {

        ViewInteraction button = Espresso.onView(ViewMatchers.withId(R.id.enter_manually_button));
        button.check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return isEnabled(); // no constraints, they are checked above
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
        );;

        // Check if the intent is the correct destination
        //Intents.intended(IntentMatchers.hasComponent("ch.epfl.sdp.healthplay"));

    }
}
