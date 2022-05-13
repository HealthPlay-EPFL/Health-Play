package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ch.epfl.sdp.healthplay.GameMenuFragment;
import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.databinding.ActivityHomeScreenBinding;

public class PlanthuntMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_main);



        Button playButton = findViewById(R.id.planthuntMainPlay);
        Button collectionButton = findViewById(R.id.planthuntMainCollection);
        Button leaveButton = findViewById(R.id.planthuntMainLeave);

        //Start main activity when clicking on Play button
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntMainActivity.this, PlanthuntCollectionActivity.class);
                startActivity(intent);
            }
        });

        //Start collection activity when clicking on Collection button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntMainActivity.this, PlanthuntCreateJoinLobbyActivity.class);
                startActivity(intent);
            }
        });

        //Leave game when clicking on Leave button
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntMainActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });
    }
}