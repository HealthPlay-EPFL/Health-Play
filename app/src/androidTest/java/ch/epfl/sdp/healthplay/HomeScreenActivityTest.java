package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
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
    public void fragmentChangeTest() {
         //ViewInteraction fragment = Espresso.onView(withId(R.id.fragmentContainerView));
         ViewInteraction navigationBar = Espresso.onView(withId(R.id.bottomNavigationView));
         //navigationBar.perform(ViewActions.click());
         //fragment.check(ViewAssertions.matches(ViewMatchers.withId(R.id.profileSetupActivity)));
    }
}
