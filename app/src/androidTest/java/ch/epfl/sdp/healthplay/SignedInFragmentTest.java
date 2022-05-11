package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.encoders.annotations.Encodable;

import junit.framework.Assert;

import org.checkerframework.checker.units.qual.Time;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.SignedInFragment;

@RunWith(AndroidJUnit4.class)
public class SignedInFragmentTest {

    @Before
    public void init() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        /**/
    }

    @Test
    public void startingNewIntentAuto() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.sign_out)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_account)).check(matches(isDisplayed()));
        onView(withId(R.id.light)).check(matches(isDisplayed()));
        onView(withId(R.id.night)).check(matches(isDisplayed()));
        onView(withId(R.id.french)).check(matches(isDisplayed()));
        onView(withId(R.id.italian)).check(matches(isDisplayed()));
        onView(withId(R.id.german)).check(matches(isDisplayed()));
        onView(withId(R.id.currentLanguage)).check(matches(withText("English")));
    }

    @Test
    public void clickOnEnglish(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.french)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppThemeFrench);
        onView(withId(R.id.currentLanguage)).check(matches(withText("Français")));
        onView(withId(R.id.english)).perform(click());
        FragmentScenario restore2 = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.currentLanguage)).check(matches(withText("English")));
    }

    @Test
    public void clickOnEnglishNight(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.night)).perform(click());
        onView(withId(R.id.french)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkThemeFrench);
        onView(withId(R.id.currentLanguage)).check(matches(withText("Français")));
        onView(withId(R.id.english)).perform(click());
        FragmentScenario restore2 = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkTheme);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.currentLanguage)).check(matches(withText("English")));
    }

    @Test
    public void clickOnFrench(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.french)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppThemeFrench);
        onView(withId(R.id.currentLanguage)).check(matches(withText("Français")));
        onView(withId(R.id.english)).perform(click());
    }

    @Test
    public void clickOnFrenchNight(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.night)).perform(click());
        onView(withId(R.id.french)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkThemeFrench);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.currentLanguage)).check(matches(withText("Français")));
        onView(withId(R.id.english)).perform(click());
    }

    @Test
    public void clickOnItalian(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.italian)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppThemeItalian);
        onView(withId(R.id.currentLanguage)).check(matches(withText("Italiano")));
        onView(withId(R.id.english)).perform(click());
    }

    @Test
    public void clickOnItalianNight(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkTheme);
        onView(withId(R.id.night)).perform(click());
        onView(withId(R.id.italian)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkThemeItalian);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.currentLanguage)).check(matches(withText("Italiano")));
        onView(withId(R.id.english)).perform(click());
    }

    @Test
    public void clickOnGerman(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.german)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppThemeGerman);
        onView(withId(R.id.currentLanguage)).check(matches(withText("Deutsch")));
        onView(withId(R.id.english)).perform(click());
    }

    @Test
    public void clickOnGermanNight(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkTheme);
        onView(withId(R.id.night)).perform(click());
        onView(withId(R.id.german)).perform(click());
        onView(withId(R.id.switchFragButton)).check(matches(isDisplayed()));
        FragmentScenario restore = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.darkThemeGerman);
        onView(withId(R.id.light)).perform(click());
        onView(withId(R.id.currentLanguage)).check(matches(withText("Deutsch")));
        onView(withId(R.id.english)).perform(click());
    }

    @Test
    public void clickOnLight(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.light)).perform(click());
        test.onFragment(new FragmentScenario.FragmentAction() {
            @Override
            public void perform(@NonNull Fragment fragment) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
                int mode = sharedPref.getInt(fragment.getContext().getString(R.string.saved_night_mode), AppCompatDelegate.MODE_NIGHT_NO);
                Assert.assertEquals(AppCompatDelegate.MODE_NIGHT_NO, mode);
            }
        });
    }

    @Test
    public void clickOnNight(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.night)).perform(click());
        test.onFragment(new FragmentScenario.FragmentAction() {
            @Override
            public void perform(@NonNull Fragment fragment) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
                int mode = sharedPref.getInt(fragment.getContext().getString(R.string.saved_night_mode), AppCompatDelegate.MODE_NIGHT_NO);
                Assert.assertEquals(AppCompatDelegate.MODE_NIGHT_YES, mode);
            }
        });
        onView(withId(R.id.light)).perform(click());
    }

    @Test
    public void signOutTest() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.sign_out)).perform(click());
        FragmentScenario test2 = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        TimeUnit.SECONDS.sleep(1);
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        TimeUnit.SECONDS.sleep(1);
    }

    /*@Test
    public void remove_account() throws InterruptedException {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        FragmentScenario test = FragmentScenario.launchInContainer(SignedInFragment.class, new Bundle(), R.style.AppTheme);
        onView(withId(R.id.sign_out)).perform(click());
        ActivityScenario activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withText("Sign in with email")).perform(click());
        onView(withHint("Email")).perform(typeText("HPTest@admin.ch"), closeSoftKeyboard());
        onView(withText("Next")).perform(click());
        onView(withHint("New password")).perform(typeText("123456"), closeSoftKeyboard());
        onView(withText("Save")).perform(click());
        TimeUnit.SECONDS.sleep(1);
        TimeUnit.SECONDS.sleep(2);
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HPTest@admin.ch", "123456");
        ActivityScenario activity2 = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.SignedInFragment);
            }
        });
        onView(withId(R.id.delete_account)).perform(click());
        onView(withText("No")).perform(click());
        onView(withId(R.id.delete_account)).perform(click());
        onView(withText("Yes, nuke it!")).perform(click());
        ActivityScenario sc = ActivityScenario.launch(ProductInfoActivity.class);
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
    }*/
}
