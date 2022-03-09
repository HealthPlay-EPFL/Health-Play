package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class GamesMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_menu);

        setTitle("Games");

        final ImageButton planthuntButton = findViewById(R.id.planthuntThumbnail);

        planthuntButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GamesMenu.this, PlanthuntDescription.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(GamesMenu.this, planthuntButton, ViewCompat.getTransitionName(planthuntButton));
                        startActivity(intent, options.toBundle());
                    }
                }
        );

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
    }
}