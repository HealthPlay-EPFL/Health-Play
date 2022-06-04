package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sdp.healthplay.database.DataCache;
import ch.epfl.sdp.healthplay.database.Database;

@RunWith(AndroidJUnit4.class)
public class HomeScreenActivityTest {
    @Before
    public void init() throws InterruptedException {
        AuthUiActivityTest.signIn(SignedInFragmentTest.emailString, SignedInFragmentTest.password);
        ActivityScenario activityScenario = ActivityScenario.launch(HomeScreenActivity.class);
    }

    @Test
    public void isOnline() {
        Database database = new Database();
        database.readField(FirebaseAuth.getInstance().getCurrentUser().getUid(), "onlineStatus", new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                assertThat(task.getResult().getValue(String.class), Matchers.equalTo("online"));
            }
        });
    }

    @Test
    public void activityInitialization() {
        Espresso.onView(withId(R.id.fragmentContainerView)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
    }
}
