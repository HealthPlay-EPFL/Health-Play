package ch.epfl.sdp.healthplay;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Gallery;

import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.bumptech.glide.load.engine.Resource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class EditProfilePictureFragmentTest {
    ActivityScenario activity;

    @Rule
    public IntentsTestRule mActivityTestRule = new IntentsTestRule(HomeScreenActivity.class);

    @Before
    public void init(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("HP@admin.ch", "123456");
        activity = ActivityScenario.launch(HomeScreenActivity.class);
        activity.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView b = activity.findViewById(R.id.bottomNavigationView);
                Navigation.findNavController(activity.findViewById(R.id.fragmentContainerView)).navigate(R.id.profileActivity);
            }
        });
        onView(withId(R.id.changeButton)).perform(click());
    }

    @Test
    public void initiate(){
        onView(withId(R.id.edit_profile_picture)).check(matches(isDisplayed()));
        onView(withId(R.id.change_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.exit_button)).check(matches(isDisplayed()));
    }

    @Test
    public void exit(){
        onView(withId(R.id.exit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.exit_button)).perform(click());
        onView(withId(R.id.changeButton)).check(matches(isDisplayed()));
    }

    @Test
    public void save_not_change(){
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).perform(click());
    }


    @Test
    public void save() throws InterruptedException {
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Instrumentation.ActivityResult result = createImageGallerySetResultStub(Activity.RESULT_OK);
        Intents.intending(expectedIntent).respondWith(result);
        onView(withId(R.id.change_button)).check(matches(isDisplayed()));
        onView(withId(R.id.change_button)).perform(click());
        Intents.intended(expectedIntent);
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.save_button)).perform(click());
    }

    @Test
    public void try_again() throws InterruptedException {
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK), hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Instrumentation.ActivityResult result = createImageGallerySetResultStub(Activity.RESULT_CANCELED);
        Intents.intending(expectedIntent).respondWith(result);
        onView(withId(R.id.change_button)).check(matches(isDisplayed()));
        onView(withId(R.id.change_button)).perform(click());
        Intents.intended(expectedIntent);
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
    }

    private Instrumentation.ActivityResult createImageGallerySetResultStub(int result) {
        Resources resource = InstrumentationRegistry.getInstrumentation().getContext().getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resource.getResourcePackageName(R.drawable.logo) + "/" +
                resource.getResourceTypeName(R.drawable.logo) + "/" +
                resource.getResourceEntryName(R.drawable.logo));
        Intent intent = new Intent();
        intent.setData(uri);
        return new Instrumentation.ActivityResult(result, intent);
    }
}
