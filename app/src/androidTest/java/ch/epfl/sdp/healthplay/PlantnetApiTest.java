package ch.epfl.sdp.healthplay;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.not;
import static ch.epfl.sdp.healthplay.api.PlantnetApi.buildUrl;
import static ch.epfl.sdp.healthplay.api.PlantnetApi.readJsonFromUrl;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
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

import ch.epfl.sdp.healthplay.api.PlantnetApi;

@RunWith(AndroidJUnit4.class)
public class PlantnetApiTest {
    private static String URL = "https://my-api.plantnet.org/v2/identify/all?api-key=2b106lfsSUXwI6p3JCdVgOXVQe&images=https%3A%2F%2Ffirebasestorage.googleapis.com%2Fv0%2Fb%2Ftest-6ab96.appspot.com%2Fo%2Fcoquelicot.jpg%3Falt%3Dmedia%26token%3D6ef1cf50-2419-4ff9-a8ef-d28ce584836b&organs=flower";
    private static String BUILT_URL = "https://my-api.plantnet.org/v2/identify/all?api-key=2b106lfsSUXwI6p3JCdVgOXVQe&images=https%253A%252F%252Ffirebasestorage.googleapis.com%252Fv0%252Fb%252Ftest-6ab96.appspot.com%252Fo%252Fcoquelicot.jpg%253Falt%253Dmedia%2526token%253D6ef1cf50-2419-4ff9-a8ef-d28ce584836b&organs=flower";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public ActivityScenarioRule<PlantnetApi> testRule = new ActivityScenarioRule<>(PlantnetApi.class);

    @Test
    public void correctUrlWorks() throws JSONException, IOException {
        JSONObject object = readJsonFromUrl(URL);
        assert(object != null);
    }

    @Test
    public void buildUrlWorks() throws JSONException, IOException {
        String result = buildUrl("2b106lfsSUXwI6p3JCdVgOXVQe",
                "https%3A%2F%2Ffirebasestorage.googleapis.com%2Fv0%2Fb%2Ftest-6ab96.appspot.com%2Fo%2Fcoquelicot.jpg%3Falt%3Dmedia%26token%3D6ef1cf50-2419-4ff9-a8ef-d28ce584836b",
                "flower");
        assert(result.equals(BUILT_URL));
    }

}
