package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.database.DataCache;

@RunWith(AndroidJUnit4.class)
public class HomeScreenActivityTest {

    @Before
    public void init(){
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        ActivityScenario sc = ActivityScenario.launch(HomeScreenActivity.class);
    }

    @Test()
    public void activityInitialization() {
         Espresso.onView(withId(R.id.fragmentContainerView)).check(matches(isDisplayed()));
         Espresso.onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
    }
}
