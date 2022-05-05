package ch.epfl.sdp.healthplay.planthunt;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntWaitLobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_wait_lobby);

        final ImageView waitView = findViewById(R.id.planthuntWaitGif);
        Glide.with(this).load(R.drawable.loading_planthunt).into(waitView);

        //Initialize database reference
        Database db = new Database();

        Intent intent = getIntent();
        String lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        String currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);
        String hostStatus = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.HOST);

        final TextView lobbyNameText = findViewById(R.id.planthuntWaitName);
        lobbyNameText.setText(lobbyName);

        List<String> usernames = new ArrayList<>();
        final TextView username1Text = findViewById(R.id.planthuntWaitText1);
        final TextView username2Text = findViewById(R.id.planthuntWaitText2);
        final TextView username3Text = findViewById(R.id.planthuntWaitText3);

        //Get player names from the database
        db.mDatabase.child(Database.LOBBIES).child(lobbyName).addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.getAllLobbyPlayerUids(lobbyName, task -> {
                            if (!task.isSuccessful()) {

                                Log.e("ERROR", "An error happened");
                            }
                            String name = Objects.requireNonNull(task.getResult().getValue()).toString();
                            if (!usernames.contains(name)){
                                usernames.add(name);
                                if (usernames.size() == 1){
                                    username1Text.setText(usernames.get(0));
                                }
                                if (usernames.size() == 2){
                                    username2Text.setText(usernames.get(1));
                                }
                                if (usernames.size() == 3){
                                    username3Text.setText(usernames.get(2));
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.toString());
                    }
                }
        );

        Button readyButton = findViewById(R.id.planthuntWaitButton);

        //Set player as ready when Ready button is clicked
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setLobbyPlayerReady(lobbyName, currentUsername);
                db.addLobbyReadyPlayer(lobbyName);
            }
        });

        //Get number of ready players
        db.mDatabase.child(Database.LOBBIES).child(lobbyName).child(Database.PLAYERS_READY).addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.getLobbyMaxPlayerCount(lobbyName, task -> {
                            if (!task.isSuccessful()) {

                                Log.e("ERROR", "An error happened");
                            }
                            String name = Objects.requireNonNull(task.getResult().getValue()).toString();
                            if (Math.toIntExact((long) snapshot.getValue()) == Math.toIntExact((long) task.getResult().getValue())){
                                Intent intent = new Intent(PlanthuntWaitLobbyActivity.this, PlanthuntLobbyActivity.class);
                                intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
                                intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, currentUsername);
                                intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST, hostStatus);
                                startActivity(intent);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.toString());
                    }
                }
        );

    }
}