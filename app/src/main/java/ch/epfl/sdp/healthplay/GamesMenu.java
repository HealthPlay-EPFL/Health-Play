package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class GamesMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_menu);
        setTitle("Games");

        //Display Planthunt description when Planthunt button image is clicked
        final ImageButton planthuntDescriptionButton = findViewById(R.id.planthuntThumbnail);
        planthuntDescriptionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GamesMenu.this, PlanthuntDescription.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(GamesMenu.this, planthuntDescriptionButton, ViewCompat.getTransitionName(planthuntDescriptionButton));
                        startActivity(intent, options.toBundle());
                    }
                }
        );

        //Display Kneetag description when Kneetag button image is clicked
        final ImageButton kneetagButton = findViewById(R.id.kneetagThumbnail);
        kneetagButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GamesMenu.this, KneetagDescription.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(GamesMenu.this, kneetagButton, ViewCompat.getTransitionName(kneetagButton));
                        startActivity(intent, options.toBundle());
                    }
                }
        );

        final Button planthuntLaunchButton = findViewById(R.id.planthuntPlay);
        planthuntLaunchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GamesMenu.this, PlantnetTest.class);
                        startActivity(intent);
                    }
                }

        );
    }
}