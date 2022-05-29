package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

//@RunWith(AndroidJUnit4.class)
public class ProfileSettingsActivityTest {
    /*@Before
    public void init(){
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        ActivityScenario activity = ActivityScenario.launch(ProfileSettingsActivity.class);
    }

    @Test
    public void saveNewInfo() throws InterruptedException {
        ViewInteraction text = Espresso.onView(withId(R.id.modifyNameEditText));
        text.perform(ViewActions.typeText(""));

        ViewActions.closeSoftKeyboard();

        text = Espresso.onView(withId(R.id.modifySurnameEditText));
        text.perform(ViewActions.typeText(""));

        ViewActions.closeSoftKeyboard();

        text = Espresso.onView(withId(R.id.modifyUsernameEditText));
        text.perform(ViewActions.typeText(""));

        ViewActions.closeSoftKeyboard();

        text = Espresso.onView(withId(R.id.modifyBirthDateEditText));
        text.perform(ViewActions.typeText(""));

        ViewActions.closeSoftKeyboard();

        text = Espresso.onView(withId(R.id.modifyWeightEditText));
        text.perform(ViewActions.typeText(""));

        ViewActions.closeSoftKeyboard();

        ViewInteraction button = Espresso.onView((withId(R.id.button2)));
        button.perform(ViewActions.click());

        TimeUnit.SECONDS.sleep(3);
    }*/

}
