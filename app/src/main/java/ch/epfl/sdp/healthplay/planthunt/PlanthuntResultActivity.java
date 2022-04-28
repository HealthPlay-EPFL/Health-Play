package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

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

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.api.PlantnetApi;
import okhttp3.HttpUrl;

public class PlanthuntResultActivity extends AppCompatActivity {
    private static String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_result);

        imageUrl = getIntent().getStringExtra("url");

        final ImageView plantView = findViewById(R.id.plantPictureImage);
        System.out.println(imageUrl);
        Glide.with(this).load(imageUrl).into(plantView);

        final Button plantButton = findViewById(R.id.plantButton);
        plantButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = findViewById(R.id.plantDescription);
                        textView.setText(getIntent().getStringExtra("name"));
                    }
                }

        );

    }


}