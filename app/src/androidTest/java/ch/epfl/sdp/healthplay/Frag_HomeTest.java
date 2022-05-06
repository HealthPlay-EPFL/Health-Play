package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
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

    @Before
    public void before() {

        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");


    }

    @Test
    public void getDateWithoutStatsTest() throws InterruptedException {

        // Force a login on the empty stats user

        TimeUnit.SECONDS.sleep(1);
        ViewInteraction sus = onView(Matchers.allOf(withId(R.id.calendar), hasChildCount(3)));
        sus.perform(ViewActions.click());


        /*onView(withId(R.id.my_date)).check(
                matches(
                        withText("No stats, please begin adding calories if you want to use the calendar summary")
                )
        );*/

    }

    /**
     * Test that the correct string is printed if there is no user logged in
     * @throws InterruptedException
     */
    /*@Test
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
    }*/

    /*@Test
    public void getDataFromFirebaseTest(){
            ViewInteraction sus = onView(Matchers.allOf(withId(R.id.calendar), hasChildCount(3)));
            sus.perform(ViewActions.click());
            onView(withId(R.id.my_date)).check(
                    matches(
                            withText("date + \": You've consumed :\" +\n" +
                                    "                            \"\\n calories: \" + String.valueOf(userStats.get(date).get(Database.CALORIE_COUNTER)) +\n" +
                                    "                            \"\\n weight: \" + String.valueOf(userStats.get(date).get(Database.WEIGHT)) +\n" +
                                    "                            \"\\n health point: \" + String.valueOf(userStats.get(date).get(Database.HEALTH_POINT)));")
                    )
            );

    }*/

    /**
     * Test if the welcome message is printed at the creation of the view
     */
    @Test
    public void getWelcomeMessage() {

        onView(withId(R.id.my_date)).check(
                matches(
                        withText("Welcome to the home page! \nChoose a date to check what you've consumed.")));


    }

    @Test
    public void goToFriendList(){
        /*
        Espresso.onView(withId(R.id.FriendList_button)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click go to friend list";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        Espresso.onView(withId(R.id.addFriendBouton)).check(matches(isDisplayed()));
    }

    @Test
    public void goToGraphs(){
        Espresso.onView(withId(R.id.switchFragButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click go to friend list";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        Espresso.onView(withId(R.id.buttonSwap)).check(matches(isDisplayed()));

         */
    }


}
