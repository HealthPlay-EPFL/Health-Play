package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;

public class PlantnetTest extends AppCompatActivity {
    private static final String API_KEY = "2b106lfsSUXwI6p3JCdVgOXVQe";
    //private static final String IMAGE = "https://www.jardiner-malin.fr/wp-content/uploads/2022/01/orchidee.jpg";
    private static final String IMAGE = "@drawable/orchidee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantnet_test);

        //https://my-api.plantnet.org/v2/identify/all?
        // api-key=2b106lfsSUXwI6p3JCdVgOXVQe&
        // images=https%3A%2F%2Fmy.plantnet.org%2Fimages%2Fimage_1.jpeg&
        // images=https%3A%2F%2Fmy.plantnet.org%2Fimages%2Fimage_2.jpeg&
        // organs=flower&
        // organs=leaf


        final Button plantButton = findViewById(R.id.plantButton);
        plantButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try  {
                                    String urlString = buildUrl(API_KEY, IMAGE, "flower");

                                    System.out.println("Requested URL:");
                                    System.out.println(urlString);

                                    JSONObject json = readJsonFromUrl(urlString);
                                    String commonNames = json.getJSONArray("results")
                                            .getJSONObject(0)
                                            .getJSONObject("species")
                                            .getJSONArray("commonNames")
                                            .toString();

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

    private String buildUrl(String apiKey, String imageUrl, String organ){
        java.net.URL url = new HttpUrl.Builder()
                .scheme("https")
                .host("my-api.plantnet.org")
                .addPathSegments("v2/identify/all")
                .addQueryParameter("api-key", "2b106lfsSUXwI6p3JCdVgOXVQe")
                .addQueryParameter("images", "https://www.jardiner-malin.fr/wp-content/uploads/2022/01/orchidee.jpg")
                .addQueryParameter("organs", "flower")
                .build().url();
        return url.toString();
    }

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

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}