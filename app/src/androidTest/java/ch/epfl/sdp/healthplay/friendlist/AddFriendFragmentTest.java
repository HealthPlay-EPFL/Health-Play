package ch.epfl.sdp.healthplay.friendlist;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;


@RunWith(AndroidJUnit4.class)
public class AddFriendFragmentTest {
    @Rule
    public ActivityScenarioRule<HomeScreenActivity> testRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Before
    public void before() throws InterruptedException{
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
        onView(ViewMatchers.withId(R.id.FriendList_button)).perform(click());
        onView(withId(R.id.addFriendBouton)).perform(click());
    }


    @Test
    public void backToFriendListTest(){

        Espresso.onView(withId(R.id.backButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click plus button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        onView(withId(R.id.addFriendBouton)).check(matches(isDisplayed()));


    }

    /*@Test
    public void listViewIsCorrectlyDisplayed(){

        onView(withId(R.id.allUserList)).check(matches(isDisplayed()));

    }

    @Test
    public void filterIsWorking(){

        onView(withId(R.id.friendSearch)).perform(ViewActions.typeText("123"));
        onData(anything()).inAdapterView(withId(R.id.allUserList));

        onView(withId(R.id.allUserList)).check(matches(new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                return listView.getCount() == 1;
            }

            @Override
            public void describeTo(Description description) {

            }

        }));


    }*/

    @Test
    public void addFriend() throws InterruptedException {
        onView(withId(R.id.friendSearch)).perform(ViewActions.typeText("123"));
        onData(anything()).inAdapterView(withId(R.id.allUserList)).atPosition(0).onChildView(withId(R.id.manageFriendButton)).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }
                    @Override
                    public String getDescription() {
                        return "click add button";
                    }
                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        Database database = new Database();
        Map<String, Boolean> map = database.getFriendList();
        TimeUnit.SECONDS.sleep(1);
        assertTrue(map.containsKey("123"));
    }




}
