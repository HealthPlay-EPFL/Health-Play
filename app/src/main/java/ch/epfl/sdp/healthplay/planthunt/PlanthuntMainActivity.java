package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

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

        //Start collection activity when clicking on Collection button
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntMainActivity.this, PlanthuntCollectionActivity.class);
                startActivity(intent);
            }
        });

        //Start main activity when clicking on Play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetIsConnected()){
                    //TODO translate ANTOINE
                    Snackbar.make(findViewById(R.id.planthuntMainLayout), "Your device is not connected to the internet", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    //TODO translate ANTOINE
                    Snackbar.make(findViewById(R.id.planthuntMainLayout), "Please log in to play", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(PlanthuntMainActivity.this, PlanthuntCreateJoinLobbyActivity.class);
                startActivity(intent);
            }
        });

        //Leave game when clicking on Leave button
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetIsConnected()){
                    //TODO translate ANTOINE
                    Snackbar.make(findViewById(R.id.planthuntMainLayout), "Your device is not connected to the internet", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    //TODO translate ANTOINE
                    Snackbar.make(findViewById(R.id.planthuntMainLayout), "Please log in to see your collection", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(PlanthuntMainActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
}