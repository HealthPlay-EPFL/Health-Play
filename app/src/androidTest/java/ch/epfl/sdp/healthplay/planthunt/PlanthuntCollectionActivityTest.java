package ch.epfl.sdp.healthplay.planthunt;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import ch.epfl.sdp.healthplay.R;

import ch.epfl.sdp.healthplay.planthunt.PlanthuntResultActivity;

public class PlanthuntCollectionActivityTest {
    private static final String expected = "https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/Planthunt%2FCdTrI7WKUUThqsVTFx6JZJZhk0s2%2Fwhatever.jpg?alt=media&token=937922cf-0744-4718-8ecf-c1abdda627c8";

    @Before
    public void before() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
    }

    /*@Test
    public void itemIsCorrectlyLaunched(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntCameraActivity.class);

        onData(allOf()).atPosition(0).
                onChildView(withId(R.id.plantCollectionList)).
                perform(click());

        Espresso.onView(withId(R.id.plantItemName)).check(matches(isDisplayed()));
    }*/

    /*
    @Test
    public void getImageUrlWorks(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntCameraActivity.class);
        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();

        String actual = CameraApi.getImageUrl(user, "whatever.jpg");
        assertEquals(actual, expected);
    }*/


    @Test
    public void itemIsCorrectlyDisplayed(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanthuntCollectionItemActivity.class);
        intent.putExtra("name", "testName");
        intent.putExtra("date", "testDate");
        intent.putExtra("image", "testImage");

        try (ActivityScenario<PlanthuntCollectionItemActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withId(R.id.planthuntLobbyButton)).check(matches(not(isDisplayed())));
        }
    }


}