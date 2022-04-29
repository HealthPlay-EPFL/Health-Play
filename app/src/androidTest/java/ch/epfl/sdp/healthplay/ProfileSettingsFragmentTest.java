package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ProfileSettingsFragmentTest {
    @Before
    public void init() {
        FragmentScenario<ProfileSettingsFragment> scenario = FragmentScenario.launchInContainer(ProfileSettingsFragment.class);
    }

    @Test
    public void enterInfo() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
        // Create view with user info
    }

    /*@Test
    public void saveNewInfo() throws InterruptedException {
        ViewInteraction text = Espresso.onView(withId(R.id.modifyNameEditText));
        String test = "test";
        text.perform(ViewActions.typeText(test), ViewActions.closeSoftKeyboard());

        test = "null";
        text = Espresso.onView(withId(R.id.modifySurnameEditText));
        text.perform(ViewActions.typeText(test), ViewActions.closeSoftKeyboard());

        text = Espresso.onView(withId(R.id.modifyUsernameEditText));
        text.perform(ViewActions.typeText(test), ViewActions.closeSoftKeyboard());

        test = "2000-01-01";
        text = Espresso.onView(withId(R.id.modifyBirthDateEditText));
        text.perform(ViewActions.typeText(test), ViewActions.closeSoftKeyboard());

        test = "65";
        text = Espresso.onView(withId(R.id.modifyWeightEditText));
        text.perform(ViewActions.typeText(test), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.button2)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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

        TimeUnit.SECONDS.sleep(3);
    }*/
}
