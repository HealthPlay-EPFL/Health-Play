package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileSettingsActivityTest {
    @Rule
    public ActivityScenarioRule<ProfileSettingsActivity> testRule =
            new ActivityScenarioRule<>(ProfileSettingsActivity.class);

    @Test
    public void enterInfo() {
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
        // Create view with user info
    }

    @Test
    public void saveNewInfo() {
        ViewInteraction text = Espresso.onView(withId(R.id.modifyNameEditText));
        text.perform(ViewActions.typeText(""));

        text = Espresso.onView(withId(R.id.modifySurnameEditText));
        text.perform(ViewActions.typeText(""));

        text = Espresso.onView(withId(R.id.modifyUsernameEditText));
        text.perform(ViewActions.typeText(""));

        text = Espresso.onView(withId(R.id.modifyBirthDateEditText));
        text.perform(ViewActions.typeText(""));

        text = Espresso.onView(withId(R.id.modifyWeightEditText));
        text.perform(ViewActions.typeText(""));

        ViewInteraction button = Espresso.onView((withId(R.id.button2)));
        button.perform(ViewActions.click());
    }

}
