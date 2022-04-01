package ch.epfl.sdp.healthplay;


import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class PlantnetApiTest {
    private static final String TEST_TEXT = "https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/20220325_100859_?alt=media&token=937922cf-0744-4718-8ecf-c1abdda627c8";

    @Rule
    public ActivityScenarioRule<PlantnetApi> testRule = new ActivityScenarioRule<>(PlantnetApi.class);

    @Test
    public void planthuntButtonCorrectlyDisplaysName() throws InterruptedException {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlantnetApi.class);
        intent.putExtra(CameraApi.EXTRA_MESSAGE, TEST_TEXT);

        Espresso.onView(withId(R.id.plantButton)).perform(click());

        Espresso.onView(withId(R.id.plantDescription)).check(matches(isDisplayed()));

        /*Espresso.onView(withId(R.id.plantDescription)).check(
                ViewAssertions.matches(
                        ViewMatchers.withText("[\"Shirley poppy\",\"Common poppy\",\"Field poppy\"]")
                )
        );*/
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test(expected=MalformedURLException.class)
    public void incorrectUrlFails() throws JSONException, IOException {
        PlantnetApi.readJsonFromUrl("incorrect URL");
    }

    @Test(expected=FileNotFoundException.class)
    public void incorrectApiKeyFails() throws JSONException, IOException {
        PlantnetApi.readJsonFromUrl("https://my-api.plantnet.org/v2/identify/all" +
                "?api-key=fake123" +
                "&images=https%3A%2F%2Ffirebasestorage.googleapis.com%2Fv0%2Fb%2Ftest-6ab96.appspot.com%2Fo%2Fcoquelicot.jpg%3Falt%3Dmedia%26token%3D6ef1cf50-2419-4ff9-a8ef-d28ce584836b" +
                "&organs=flower");
    }

    @Test(expected=FileNotFoundException.class)
    public void incorrectImageFails() throws JSONException, IOException {
        PlantnetApi.readJsonFromUrl("https://my-api.plantnet.org/v2/identify/all" +
                "?api-key=2b106lfsSUXwI6p3JCdVgOXVQe" +
                "&images=fake123" +
                "&organs=flower");
    }

    @Test(expected=FileNotFoundException.class)
    public void incorrectOrganFails() throws JSONException, IOException {
        PlantnetApi.readJsonFromUrl("https://my-api.plantnet.org/v2/identify/all" +
                "?api-key=2b106lfsSUXwI6p3JCdVgOXVQe" +
                "&images=https%3A%2F%2Ffirebasestorage.googleapis.com%2Fv0%2Fb%2Ftest-6ab96.appspot.com%2Fo%2Fcoquelicot.jpg%3Falt%3Dmedia%26token%3D6ef1cf50-2419-4ff9-a8ef-d28ce584836b" +
                "&organs=fake123");
    }


}
