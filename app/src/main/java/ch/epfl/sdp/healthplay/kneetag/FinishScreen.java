package ch.epfl.sdp.healthplay.kneetag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ch.epfl.sdp.healthplay.HomeScreenActivity;
import ch.epfl.sdp.healthplay.KneetagDescriptionFragment;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.WelcomeScreenActivity;


public class FinishScreen extends AppCompatActivity {
    public static String WINNER = "ch.epfl.sdp.healthplay.kneetag.FinishScreen.WINNER";

    public FinishScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishscreen);
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


