package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
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


public class ProfileSetupActivityTest {
    @Rule
    public ActivityScenarioRule<ProfileSetupActivity> testRule = new ActivityScenarioRule<>(ProfileSetupActivity.class);

    @Test
    public void intentContainsCorrectTextWhenButtonPressed() {

        Intents.init();
        ViewInteraction nameView = Espresso.onView(withId(R.id.ProfileName));
        ViewInteraction idView = Espresso.onView(withId(R.id.ProfileId));

        idView.perform(ViewActions.typeText("123"));
        nameView.perform(ViewActions.typeText("Hugo"));

        Espresso.closeSoftKeyboard();

        ViewInteraction button = Espresso.onView(withId(R.id.ReadyButton));
        button.perform(ViewActions.click());

        Intents.intended(toPackage("ch.epfl.sdp.healthplay"));
        Intents.intended(hasExtra(ProfileSetupActivity.EXTRA_ID, "123"));
        Intents.intended(hasExtra(ProfileSetupActivity.EXTRA_USERNAME, "Hugo"));

        Intents.release();
    }


}
