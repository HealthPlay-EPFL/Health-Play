package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.annotation.NonNull;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.SignedInActivity;
import ch.epfl.sdp.healthplay.database.User;

@RunWith(AndroidJUnit4.class)
public class Frag_HomeTest {
    @Rule
    public ActivityScenarioRule<HomeScreenActivity> testRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Test
    public void getDateWithoutStatsTest() throws InterruptedException {
        // Force a login on the empty stats user
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();

        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword("dont-delete@gmail.com", "123456");
        TimeUnit.SECONDS.sleep(3);
        ViewInteraction sus = onView(Matchers.allOf(withId(R.id.calendar), hasChildCount(3)));
        sus.perform(ViewActions.click());
        onView(withId(R.id.my_date)).check(
                matches(
                        withText("No stats, please begin adding calories if you want to use the calendar summary")
                )
        );
    }

    /**
     * Test that the correct string is printed if there is no user logged in
     * @throws InterruptedException
     */
    @Test
    public void getDateNoUserTest() throws InterruptedException {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
        TimeUnit.SECONDS.sleep(3);
        ViewInteraction sus = onView( Matchers.allOf(withId(R.id.calendar),hasChildCount(3)));
        sus.perform(ViewActions.click());
        onView(withId(R.id.my_date)).check(
                matches(
                        withText("Please login")
                )
        );
    }

    /**
     * Test if the welcome message is printed at the creation of the view
     */
    @Test
    public void getWelcomeMessage() {
        onView(withId(R.id.my_date)).check(
                matches(
                        withText("Welcome to the home page! \nChoose a date to check what you've consumed.")));
    }
}