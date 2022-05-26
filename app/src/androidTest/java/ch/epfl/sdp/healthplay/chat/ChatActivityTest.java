package ch.epfl.sdp.healthplay.chat;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

import static java.util.EnumSet.allOf;

import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Checks;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.WelcomeScreenActivity;
import ch.epfl.sdp.healthplay.database.DataCache;
import ch.epfl.sdp.healthplay.database.Friend;
import ch.epfl.sdp.healthplay.friendlist.FriendListItemActivity;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {

    @Before
    public void before() throws InterruptedException{
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
        WelcomeScreenActivity.cache = new DataCache(ApplicationProvider.getApplicationContext());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        onView(ViewMatchers.withId(R.id.FriendList_button)).perform(click());
        onData(friendWithId("H7ZFXooYVWfASQfE5R3ej8PU1B33")).inAdapterView(withId(R.id.friendList)).onChildView(withId(R.id.goToChat)).perform(click());
    }

    @Test
    public void onResume() {
        onView(withId(R.id.onlinetv)).check(matches(withText("online")));
    }

    @Test
    public void isTyping(){
        onView(withId(R.id.messaget)).perform(typeText("a"));
        onView(withId(R.id.onlinetv)).check(matches(withText("Typing....")));

        //Check that sending a message reset your status to online
        onView(withId(R.id.messaget)).perform(clearText());
        onView(withId(R.id.sendmsg)).perform(click());
        onView(withId(R.id.onlinetv)).check(matches(withText("online")));
    }


    public static Matcher<Object> friendWithId(String expectedName) {
        Checks.checkNotNull(expectedName);
        return friendWithId(equalTo(expectedName));
    }

    public static Matcher<Object> friendWithId(final Matcher<String> itemMatcher) {
        Checks.checkNotNull(itemMatcher);

        return new BoundedMatcher<Object, Friend>(Friend.class) {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("Friend with name: ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(Friend friend) {
                return itemMatcher.matches(friend.getUserId());
            }
        };
    }
}