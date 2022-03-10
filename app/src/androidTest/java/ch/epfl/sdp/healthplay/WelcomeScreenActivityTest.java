package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class WelcomeScreenActivityTest {
    /*@Rule
    public ActivityScenarioRule<WelcomeScreen> testRule = new ActivityScenarioRule<>(WelcomeScreen.class);*/

    @Test
    public void startingNewIntentAuto() throws InterruptedException {
        ActivityScenario a = ActivityScenario.launch(WelcomeScreenActivity.class);
        TimeUnit.SECONDS.sleep(6);
        onView(withId(R.id.main_view)).check(matches(isDisplayed()));
        //onView(withId(R.id.my_view)).check(matches(isDisplayed()));
        /*ViewInteraction view = Espresso.onView(withId(R.id.my_view));
        view.perform(ViewActions.click());

        Intents.init();

        // This line checks that the intent has the correct key and the correct test name
        Intents.intended(toPackage("ch.epfl.sdp.healthplay"));
        Intents.intended();

        Intents.release();*/
    }
    /*@Test
    public void startingNewIntentWithTouching() throws InterruptedException {
        ActivityScenario a = ActivityScenario.launch(WelcomeScreenActivity.class);
        onView(withId(R.id.my_view)).perform(ViewActions.click());
        onView(withId(R.id.main_view)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void pulseTest(){
        ViewInteraction imageView = Espresso.onView(withId(R.id.logo_image));

        }
*/
}
