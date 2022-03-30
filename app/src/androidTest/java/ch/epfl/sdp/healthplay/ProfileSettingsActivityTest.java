package ch.epfl.sdp.healthplay;

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
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
        // Create view with user info
    }

}
