package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ch.epfl.sdp.healthplay.R;

public class PlanthuntNewPlantActivity extends AppCompatActivity {

    private static String imageUrl;
    ImageView plantView;
    TextView textView;
    boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_new_plant);

        Intent intent = getIntent();
        String lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        String currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);

        imageUrl = getIntent().getStringExtra(PlanthuntLobbyActivity.URL);
        System.out.println(imageUrl);

        //Glide.with(PlanthuntNewPlantActivity.this).load(imageUrl).into(plantView);

        plantView = findViewById(R.id.planthuntPlantImage);
        textView = findViewById(R.id.planthuntPlantName);

        final Button plantButton = findViewById(R.id.planthuntPlantButton);
        plantButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PlanthuntNewPlantActivity.this, PlanthuntLobbyActivity.class);
                        intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
                        intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, currentUsername);
                        startActivity(intent);
                    }
                }
        );

        //isLoaded.add

        loadImageAndName();
    }

    private void loadImageAndName(){
        //Creates a new Thread to update timer asynchronously
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(100);

                        textView.setText("You found a " + getIntent().getStringExtra(PlanthuntLobbyActivity.NAME) + "!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}