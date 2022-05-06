package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.AuthUiActivity;

@RunWith(AndroidJUnit4.class)
public class AuthUiActivityTest {

    @Before
    public void init(){
        ActivityScenario activityTest = ActivityScenario.launch(AuthUiActivity.class);
    }
    @Test
    public void authenticate()  {
        init();
    }

}
