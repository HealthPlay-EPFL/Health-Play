package ch.epfl.sdp.healthplay;

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
import static org.junit.Assert.*;

import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.database.Database;

@RunWith(AndroidJUnit4.class)
public class FriendList_FragTest {

    private int numberOfFriends;
    @Rule
    public ActivityScenarioRule<HomeScreenActivity> testRule = new ActivityScenarioRule<>(HomeScreenActivity.class);

    @Before
    public void before() throws InterruptedException{
        /*
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
        Espresso.onView(withId(R.id.FriendList_button)).perform(ViewActions.click());
        Database database = new Database();
        Map<String, Boolean> map = database.getFriendList();
        List<String> friends = new ArrayList<>();
        TimeUnit.SECONDS.sleep(1);
        for (String friendId: map.keySet()
        ) {
            if(map.get(friendId).booleanValue()){
                friends.add(friendId);
            }
        }
        numberOfFriends = friends.size();

         */
    }

    @Test
    public void backToCalendarTest(){
        /*
        Espresso.onView(withId(R.id.friendToCalendar)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click Calendar button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        Espresso.onView(withId(R.id.FriendList_button)).check(matches(isDisplayed()));

         */
    }

    @Test
    public void goToAddFriendFragTest(){
        /*
        Espresso.onView(withId(R.id.addFriendBouton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return "click add friend button";
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
        Espresso.onView(withId(R.id.backButton)).check(matches(isDisplayed()));

         */
    }

    @Test
    public void listViewIsCorrectlyDisplayed(){
        /*
        Espresso.onView(withId(R.id.friendList)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.friendList)).perform(ViewActions.swipeUp());

         */
    }

    @Test
    public void removeFriend() {
        /*
        onData(anything()).inAdapterView(withId(R.id.friendList)).atPosition(0).onChildView(withId(R.id.removeFriendButton)).perform(click());
        onView(withId(R.id.friendList)).check(matches(new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;
                boolean output = listView.getCount() == numberOfFriends - 1;
                if(output) {
                    numberOfFriends -= 1;
                }
                return output;
            }

            @Override
            public void describeTo(Description description) {

            }

        }));

         */
    }

    @Test
    public void listViewDisplayAllFriends() throws InterruptedException {
        /*
        onView(withId(R.id.friendList)).check(matches(new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                return listView.getCount() == numberOfFriends;
            }

            @Override
            public void describeTo(Description description) {

            }

        }));

         */

    }


}