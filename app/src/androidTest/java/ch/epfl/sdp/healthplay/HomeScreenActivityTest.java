package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeScreenActivityTest {
    @Rule
    public ActivityScenarioRule<HomeScreenActivity> testRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Test
    public void fragmentChangeTest() {
         //ViewInteraction fragment = Espresso.onView(withId(R.id.fragmentContainerView));
         ViewInteraction navigationBar = Espresso.onView(withId(R.id.bottomNavigationView));
         navigationBar.perform(ViewActions.click());
         //fragment.check(ViewAssertions.matches(ViewMatchers.withId(R.id.profileSetupActivity)));
    }
}
