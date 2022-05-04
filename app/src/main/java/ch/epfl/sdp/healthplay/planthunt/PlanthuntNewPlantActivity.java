package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sdp.healthplay.R;

public class PlanthuntNewPlantActivity extends AppCompatActivity {
    private static String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_new_plant);


        imageUrl = getIntent().getStringExtra(PlanthuntLobbyActivity.URL);
        System.out.println(imageUrl);

        final ImageView plantView = findViewById(R.id.planthuntPlantImage);
        //Glide.with(this).load(imageUrl).into(plantView);

        final Button plantButton = findViewById(R.id.planthuntPlantButton);
        plantButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = findViewById(R.id.planthuntPlantName);
                        textView.setText("You found a " + getIntent().getStringExtra("name") + "!");
                    }
                }
        );
    }
}