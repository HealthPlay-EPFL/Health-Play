package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Lobby;

public class PlanthuntLobbyActivityTest {

    @Before
    public void before() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
    }

    @Test
    public void timerCorrectlyAppears() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntLobbyActivity.class);
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, "test");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, "a");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, PlanthuntCreateJoinLobbyActivity.HOST);

        try (ActivityScenario<PlanthuntLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.planthuntLobbyLayout)).check(matches(isDisplayed()));
        }
    }


    @Test
    public void cameraCorrectlyLaunches() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntLobbyActivity.class);
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, "test");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, "a");
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, PlanthuntCreateJoinLobbyActivity.HOST);
        PlanthuntLobbyActivity.isTested = true;
        try (ActivityScenario<PlanthuntLobbyActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.planthuntLobbyButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        }
    }


    @Test
    public void gettersCorrectlyWork() {
        Lobby lobby = new Lobby("name", "password", "host", 300, 1, 0);
        assertEquals(lobby.getMaxNbrPlayers(), 1);
        assertEquals(lobby.getNbrPlayers(), 1);
        assertEquals(lobby.getPlayersReady(), 0);
        assertEquals(lobby.getPlayersGone(), 0);
        assertEquals(lobby.getPlayerScore1(), 0);
        assertEquals(lobby.getPlayerScore2(), 0);
        assertEquals(lobby.getPlayerScore3(), 0);
        assertEquals(lobby.getPlayerUid1(), PlanthuntCreateJoinLobbyActivity.HOST);
        assertEquals(lobby.getPlayerUid2(), "");
        assertEquals(lobby.getPlayerUid3(), "");
    }

    /*
    @Test
    public void backButtonCorrectlyGoesBack() {
        Espresso.pressBack();
    }
     */


}