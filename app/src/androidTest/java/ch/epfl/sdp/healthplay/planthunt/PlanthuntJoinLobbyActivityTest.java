package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntJoinLobbyActivity;

public class PlanthuntJoinLobbyActivityTest {
    private static final String NAME = "test";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "b";
    private static final String USERNAME_2 = "c";


    @Rule
    public ActivityScenarioRule<PlanthuntJoinLobbyActivity> testRule = new ActivityScenarioRule<>(PlanthuntJoinLobbyActivity.class);

    @Before
    public void init(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com", "123456");
    }

    @Test
    public void lobbyIsCorrectlyJoined() {
        ViewInteraction textName = Espresso.onView(ViewMatchers.withId(R.id.planthuntJoinLobbyName));
        textName.perform(ViewActions.typeText(NAME));
        Espresso.closeSoftKeyboard();
        ViewInteraction textPassword = Espresso.onView(withId(R.id.planthuntJoinLobbyPassword));
        textPassword.perform(ViewActions.typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        ViewInteraction textUsername = Espresso.onView(withId(R.id.planthuntJoinLobbyUsername));
        textUsername.perform(ViewActions.typeText(USERNAME));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.planthuntJoinLobbyButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        Espresso.onView(withId(R.id.planthuntJoinLobbyButton)).check(matches(isDisplayed()));
    }

    @Test
    public void lobbyIsCorrectlyJoinedTwice() {
        ViewInteraction textName = Espresso.onView(ViewMatchers.withId(R.id.planthuntJoinLobbyName));
        textName.perform(ViewActions.typeText(NAME));
        Espresso.closeSoftKeyboard();
        ViewInteraction textPassword = Espresso.onView(withId(R.id.planthuntJoinLobbyPassword));
        textPassword.perform(ViewActions.typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        ViewInteraction textUsername = Espresso.onView(withId(R.id.planthuntJoinLobbyUsername));
        textUsername.perform(ViewActions.typeText(USERNAME_2));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.planthuntJoinLobbyButton)).check(matches(allOf( isEnabled(), isClickable()))).perform(
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
        Espresso.onView(withId(R.id.planthuntJoinLobbyButton)).check(matches(isDisplayed()));
    }

    @Test
    public void backButtonCorrectlyGoesBack() {
        Espresso.pressBack();
    }
}
