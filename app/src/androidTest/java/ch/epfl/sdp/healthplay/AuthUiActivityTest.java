package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class AuthUiActivityTest {

    @Before
    public void init() throws InterruptedException {
        FirebaseAuth.getInstance().signOut();
        ActivityScenario init = ActivityScenario.launch(WelcomeScreenActivity.class);
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        ActivityScenario home = ActivityScenario.launch(HomeScreenActivity.class);
        home.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
    }

    @Test
    public void authenticate() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText(SignedInFragmentTest.emailString), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(2);
        onView(withHint("Password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("SIGN IN")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        //FirebaseAuth.getInstance().signInWithEmailAndPassword(SignedInFragmentTest.emailString, SignedInFragmentTest.password);
        ActivityScenario sc2 = ActivityScenario.launch(HomeScreenActivity.class);
        sc2.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.sign_out)).perform(click());
    }

    @Test
    public void remove_account() throws InterruptedException {
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText("HPTest@admin.ch"), ViewActions.closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        TimeUnit.SECONDS.sleep(2);
        onView(withHint("New password")).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withText("Save")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        ActivityScenario sc2 = ActivityScenario.launch(HomeScreenActivity.class);
        sc2.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.delete_account)).perform(click());
        onView(withText("Yes, nuke it!")).perform(click());
    }

}
