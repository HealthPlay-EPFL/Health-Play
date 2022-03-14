package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.AuthUiActivity;
import ch.epfl.sdp.healthplay.auth.SignedInActivity;

@RunWith(AndroidJUnit4.class)
public class AuthUiActivityTest {

    @Before
    public void init(){
        ActivityScenario activityTest = ActivityScenario.launch(AuthUiActivity.class);
    }
    @Test
    public void startingNewIntentAuto() throws InterruptedException {
        ViewInteraction button = Espresso.onView(withId(R.id.sign_out));
        button.perform(ViewActions.click());
    }
}
