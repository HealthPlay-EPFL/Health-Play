package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class BarcodeScanFragmentTest {

    @Test
    public void testEnterManually() throws InterruptedException {
        WelcomeScreenActivity.cache = new DataCache(InstrumentationRegistry.getInstrumentation().getContext());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.barcodescanActivity);

            }
        });
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.enter_manually_button)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_manually_button)).perform(click());
        onView(withId(R.id.findProductInfos)).check(matches(isDisplayed()));
        onView(withId(R.id.barcodeText)).check(matches(isDisplayed()));
    }

    @Test
    public void testScan() throws InterruptedException {
        WelcomeScreenActivity.cache = new DataCache(InstrumentationRegistry.getInstrumentation().getContext());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.barcodescanActivity);

            }
        });
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.get_information_from_barcode)).check(matches(isDisplayed()));
        onView(withId(R.id.get_information_from_barcode)).perform(click());
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
    }
}
