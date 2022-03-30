package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private static final String API_KEY = "2b106lfsSUXwI6p3JCdVgOXVQe";
    private static String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantnet_api);

        imageUrl = getIntent().getStringExtra(CameraApi.EXTRA_MESSAGE);

        final ImageView plantView = findViewById(R.id.plantPictureImage);
        System.out.println(imageUrl);
        Glide.with(this).load(imageUrl).into(plantView);

        final Button plantButton = findViewById(R.id.plantButton);
        plantButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Creates a new Thread to receive Url response asynchronously
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try  {
                                    //Returns built URL with given image link
                                    String urlString = buildUrl(API_KEY, imageUrl, "flower");
                                    System.out.println(urlString);
                                    //Gets JSON object from built URL
                                    JSONObject json = readJsonFromUrl(urlString);

                                    //Extracts plant name from received JSON
                                    String commonNames = json.getJSONArray("results")
                                            .getJSONObject(0)
                                            .getJSONObject("species")
                                            .getJSONArray("commonNames")
                                            .toString();

                                    //Asynchronously outputs extracted name to text field
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView textView = findViewById(R.id.plantDescription);
                                            textView.setText(commonNames);
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();

                    }
                }

        );

    }

    /**
     * Creates Pl@ntnet API request URL from given parameters and file URL.
     *
     * @param apiKey the key required by the API to answer the request
     * @param imageUrl the URL of the plant image we want to identify
     * @param organ the kind of plant we want to identify
     * @return the built URL
     */
    private static String buildUrl(String apiKey, String imageUrl, String organ){
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

    //Opens InputStream from URL and returns a new JSON object initialized with the InputStream content
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

    //Returns the content of the BufferedReader concatenated into a single string
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}