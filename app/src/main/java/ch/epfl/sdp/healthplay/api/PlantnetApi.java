package ch.epfl.sdp.healthplay.api;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;

public class PlantnetApi extends AppCompatActivity {

    public static final String API_KEY = "2b106lfsSUXwI6p3JCdVgOXVQe";

    /**
     * Creates Pl@ntnet API request URL from given parameters and file URL.
     *
     * @param apiKey the key required by the API to answer the request
     * @param imageUrl the URL of the plant image we want to identify
     * @param organ the kind of plant we want to identify
     * @return the built URL
     */
    public static String buildUrl(String apiKey, String imageUrl, String organ){
        java.net.URL url = new HttpUrl.Builder()
                .scheme("https")
                .host("my-api.plantnet.org")
                .addPathSegments("v2/identify/all")
                .addQueryParameter("api-key", apiKey)
                .addQueryParameter("images", imageUrl)
                .addQueryParameter("organs", organ)
                .build().url();
        return url.toString();
    }

    /**
    * Opens InputStream from URL and returns a new JSON object initialized with the InputStream content
    * */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    /**
     * Returns the content of the BufferedReader concatenated into a single string
     * */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
