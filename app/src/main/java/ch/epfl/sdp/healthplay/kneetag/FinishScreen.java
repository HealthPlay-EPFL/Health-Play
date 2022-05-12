package ch.epfl.sdp.healthplay.kneetag;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


import ch.epfl.sdp.healthplay.HomeScreenActivity;

import ch.epfl.sdp.healthplay.R;



public class FinishScreen extends AppCompatActivity {
    public static String WINNER = "ch.epfl.sdp.healthplay.kneetag.FinishScreen.WINNER";

    public FinishScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaPlayer.create(this, R.raw.notification).start();
        setContentView(R.layout.activity_gameended);
        //display the winner
        TextView text=findViewById(R.id.winner_display);
        text.setText(getString(R.string.winnerMessage)+ getIntent().getStringExtra(WINNER));
        //rerun the game
        findViewById(R.id.restart).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FinishScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        //go back to the menu
        findViewById(R.id.quit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FinishScreen.this, HomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }
}


