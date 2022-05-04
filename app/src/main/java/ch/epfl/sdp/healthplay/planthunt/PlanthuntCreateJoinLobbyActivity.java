package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ch.epfl.sdp.healthplay.R;

public class PlanthuntCreateJoinLobbyActivity extends AppCompatActivity {

    public static final String LOBBY_NAME = "LOBBY_NAME";
    public static final String USERNAME = "USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_create_join_lobby);


        Button createButton = findViewById(R.id.planthuntCreateLobbyMain);
        Button joinButton = findViewById(R.id.planthuntJoinLobbyMain);

        //Start CreateLobby activity when clicking on Create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntCreateJoinLobbyActivity.this, PlanthuntCreateLobbyActivity.class);
                startActivity(intent);
            }
        });

        //Start JoinLobby activity when clicking on Join button
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntCreateJoinLobbyActivity.this, PlanthuntJoinLobbyActivity.class);
                startActivity(intent);
            }
        });

    }
}