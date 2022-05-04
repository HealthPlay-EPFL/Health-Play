package ch.epfl.sdp.healthplay.planthunt;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntLobbyActivity extends AppCompatActivity {


    private final Database db = new Database();
    private String lobbyName;
    private int remainingTime = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_lobby);

        Intent intent = getIntent();
        lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        String currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);

        final TextView lobbyNameText = findViewById(R.id.planthuntLobbyName);
        lobbyNameText.setText(lobbyName);

        final TextView lobbyTimeText = findViewById(R.id.planthuntLobbyTimeText);
        final ProgressBar lobbyTimeBar = findViewById(R.id.planthuntLobbyTimeBar);

        db.mDatabase.child(Database.LOBBIES).child(lobbyName).child(Database.REMAINING_TIME).addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int time = Math.toIntExact((long) snapshot.getValue());
                        lobbyTimeText.setText(round(time/60) + ":" + (time%60));
                        lobbyTimeBar.setProgress(time);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.toString());
                    }
                }

        );

        StartTimer();
    }

    private void StartTimer(){
        //Creates a new Thread to update timer asynchronously
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 301; i++) {
                    try {
                        Thread.sleep(1000);
                        db.updateLobbyTime(lobbyName, remainingTime);
                        remainingTime--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}