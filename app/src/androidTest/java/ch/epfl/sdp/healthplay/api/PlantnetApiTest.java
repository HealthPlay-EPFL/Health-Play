package ch.epfl.sdp.healthplay.api;

import static org.junit.Assert.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


@RunWith(AndroidJUnit4.class)
public class PlantnetApiTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test(expected= MalformedURLException.class)
    public void incorrectUrlFails() throws JSONException, IOException {
        PlantnetApi.readJsonFromUrl("incorrect URL");
    }

    @Test(expected= FileNotFoundException.class)
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


    public void incorrectUrlDoesntReturnJson() throws JSONException, IOException {
        PlantnetApi.readJsonFromUrl("incorrect URL");
    }
}