package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.app.Activity;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.A;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ProfileSettingsFragmentTest {
    ActivityScenario activity;

    @Before
    public void init(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        activity = ActivityScenario.launch(HomeScreenActivity.class);
        onView( allOf( withId(R.id.profileActivity), isDescendantOfA(withId(R.id.bottomNavigationView)))).perform(click());
        onView(withId(R.id.statsButton)).perform(click());
    }

    @Test
    public void saveNewInfo() {
        String testName = "test";
        onView(withId(R.id.modifyNameEditText)).perform(typeText(testName), ViewActions.closeSoftKeyboard());

        String testSurname = "null";
        onView(withId(R.id.modifySurnameEditText)).perform(typeText(testSurname), ViewActions.closeSoftKeyboard());

        String testUsername = "SuperTest";
        onView(withId(R.id.modifyUsernameEditText)).perform(typeText(testUsername), ViewActions.closeSoftKeyboard());

        String testBirthday = "06/05/2000";
        onView(withId(R.id.modifyBirthDateEditText)).perform(typeText(testBirthday), ViewActions.closeSoftKeyboard());

        String testWeight = "65";
        onView(withId(R.id.modifyWeightEditText)).perform(typeText(testWeight), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.profileName)).check(matches(withText(testName + " " + testSurname)));
        onView(withId(R.id.profileUsername)).check(matches(withText(testUsername)));
        onView(withId(R.id.profileWeight)).check(matches(withText(testWeight)));
        onView(withId(R.id.statsButton)).perform(click());
        onView(withId(R.id.button2)).perform(click());
    }

    /*@Test
    public void firstTime() throws InterruptedException {
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.sign_out)).perform(click());
        ActivityScenario activityScenario = ActivityScenario.launch(HomeScreenActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText("HPTest@admin.ch"), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        onView(withHint("New password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("Save")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        ActivityScenario newScenario = ActivityScenario.launch(WelcomeScreenActivity.class);
        ActivityScenario sc = ActivityScenario.launch(HomeScreenActivity.class);
        sc.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.profileActivity);
            }
        });
        onView(withId(R.id.statsButton)).perform(click());
        String testName = "test";
        onView(withId(R.id.modifyNameEditText)).perform(typeText(testName), ViewActions.closeSoftKeyboard());

        String testSurname = "null";
        onView(withId(R.id.modifySurnameEditText)).perform(typeText(testSurname), ViewActions.closeSoftKeyboard());

        String testUsername = "SuperTest";
        onView(withId(R.id.modifyUsernameEditText)).perform(typeText(testUsername), ViewActions.closeSoftKeyboard());

        String testBirthday = "06/05/2000";
        onView(withId(R.id.modifyBirthDateEditText)).perform(typeText(testBirthday), ViewActions.closeSoftKeyboard());

        String testWeight = "65";
        onView(withId(R.id.modifyWeightEditText)).perform(typeText(testWeight), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.button2)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        sc.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.delete_account)).perform(click());
        onView(withText("Yes, nuke it!")).perform(click());
    }*/
}
