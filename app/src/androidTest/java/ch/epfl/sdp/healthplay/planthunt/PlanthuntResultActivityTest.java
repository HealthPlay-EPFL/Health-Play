package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.content.Intent;
import android.view.View;

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

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntResultActivityTest {

    @Before
    public void before() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
        Database db = new Database();
        db.deleteLobby("test");
        db.writeNewLobbyNoActivity("test", "password", "host", 300, 2);
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void buttonCorrectlyAppears() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntResultActivity.class);
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, "test");

        try (ActivityScenario<PlanthuntResultActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.planthuntResultButton)).check(matches(isDisplayed()));
        }

    }

    @Test
    public void buttonCorrectlyWorks() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntResultActivity.class);
        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, "test");

        try (ActivityScenario<PlanthuntResultActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.planthuntResultButton)).check(matches(allOf(isEnabled(), isClickable()))).perform(
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

            Espresso.onView(withId(R.id.planthuntMainPlay)).check(matches(isDisplayed()));
        }
    }

}