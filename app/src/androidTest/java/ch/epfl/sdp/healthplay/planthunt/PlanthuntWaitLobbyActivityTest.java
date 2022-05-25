package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Checks;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Friend;

public class PlanthuntWaitLobbyActivityTest {


    @Before
    public void before() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntWaitLobbyActivity.class);

        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, "test");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, "a");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, PlanthuntCreateJoinLobbyActivity.HOST);
        ActivityScenario.launch(intent);
    }

    //@Rule
    //public ActivityScenarioRule<PlanthuntWaitLobbyActivity> testRule = new ActivityScenarioRule<>(PlanthuntWaitLobbyActivity.class);

    @Test
    public void waitScreenIsCorrectlyLoaded() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntWaitLobbyActivity.class);

        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, "test");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, "a");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, PlanthuntCreateJoinLobbyActivity.HOST);

        try (ActivityScenario<PlanthuntWaitLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.planthuntWaitButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void inviteFriendToLobbyTest() {
        onView(withId(R.id.createInvitation)).perform(click());
        onData(anything()).inRoot(isDialog()).atPosition(0).perform(ViewActions.click());
    }

    @Test
    public void closeInvitationTest() {
        onView(withId(R.id.createInvitation)).perform(click());
        onView(ViewMatchers.withText("Cancel")).perform(click());
    }

}