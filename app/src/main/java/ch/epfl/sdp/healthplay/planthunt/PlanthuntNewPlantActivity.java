package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntNewPlantActivity extends AppCompatActivity {

    ImageView plantView;
    TextView textView;
    String lobbyName;
    boolean isLoaded = false;

    //Initialize database reference
    Database db = new Database();

    @Override
    public void onBackPressed() {
        db.addLobbyGonePlayer(lobbyName);
        Intent intent = new Intent(PlanthuntNewPlantActivity.this, PlanthuntMainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_new_plant);

        Intent intent = getIntent();
        lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        String currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);
        int points = intent.getIntExtra(PlanthuntLobbyActivity.POINTS, 0);

        //Initialize database reference
        Database db = new Database();
        db.getLobbyPlayerScore(lobbyName, currentUsername, task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "An error happened");
            }
            db.updateLobbyPlayerScore(lobbyName, currentUsername, Math.toIntExact((long) Objects.requireNonNull(task.getResult().getValue())) + points);
        });

        String plantName = "You found a " + intent.getStringExtra(PlanthuntLobbyActivity.NAME) + "!";

        textView = findViewById(R.id.planthuntPlantName);
        textView.setText(plantName);

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
    }
}