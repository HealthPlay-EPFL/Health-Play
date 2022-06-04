package ch.epfl.sdp.healthplay.planthunt;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntWaitLobbyActivity extends AppCompatActivity {
    //Initialize database reference
    Database db = new Database();
    String lobbyName, hostStatus;
    boolean isReady = false;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlanthuntWaitLobbyActivity.this, PlanthuntMainActivity.class);
        if (hostStatus.equals(PlanthuntCreateJoinLobbyActivity.HOST)){
            db.deleteLobby(lobbyName);
        }
        else{
            db.addLobbyGonePlayer(lobbyName);
        }
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_wait_lobby);

        final ImageView waitView = findViewById(R.id.planthuntWaitGif);
        Glide.with(this).load(R.drawable.loading_planthunt).into(waitView);

        Intent intent = getIntent();
        lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        String currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);
        hostStatus = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE);

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
                            if (!task.isSuccessful() || task.getResult().getValue() == null) {
                                Log.e("ERROR", "Error getting lobby player uids");
                                return;
                            }
                            String name = task.getResult().getValue().toString();
                            if (!usernames.contains(name) && name.length() > 0){
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
                if (!isReady){
                    db.addLobbyReadyPlayer(lobbyName);
                    isReady = true;
                }
            }
        });

        //Get number of ready players
        db.mDatabase.child(Database.LOBBIES).child(lobbyName).child(Database.PLAYERS_READY).addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.getLobbyPlayerCount(lobbyName, Database.MAX_NBR_PLAYERS, task -> {
                            if (!task.isSuccessful() || snapshot.getValue() == null) {
                                Log.e("ERROR", "Error getting lobby player count");
                                return;
                            }
                            String name = task.getResult().getValue().toString();
                            if (snapshot.getValue() != null){
                                if (Math.toIntExact((long) snapshot.getValue()) == Math.toIntExact((long) task.getResult().getValue())){
                                    Intent intent = new Intent(PlanthuntWaitLobbyActivity.this, PlanthuntLobbyActivity.class);
                                    intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
                                    intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, currentUsername);
                                    intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, hostStatus);
                                    startActivity(intent);
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

        FloatingActionButton invitationButton = findViewById(R.id.createInvitation);
        //Only the host can invite people to the lobby
        if(!hostStatus.equals(PlanthuntCreateJoinLobbyActivity.HOST)){
            invitationButton.setVisibility(View.INVISIBLE);
        }
        //Handle the click on the "+" button
        invitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                Context context = PlanthuntWaitLobbyActivity.this;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.choose_a_friend_to_invite_en));

                db.readField(
                        mAuth.getCurrentUser().getUid(), "friends",

                        new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("ERROR", "EREREREROOORORO");
                                } else {
                                    List<String> friendUsername = new ArrayList<>();
                                    List<String> friendId = new ArrayList<>();
                                    Map<String, String> friends =
                                            (Map<String, String>) task.getResult().getValue();
                                    if (friends != null) {
                                        for (String friend : friends.keySet()) {
                                            friendUsername.add(friends.get(friend));
                                            friendId.add(friend);
                                        }
                                        ArrayAdapter<String> adapterFriend = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1 , friendUsername);

                                        builder.setAdapter(adapterFriend, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Log.e("RECEIVER", friendId.get(which) + " " + friendId.size() );
                                                db.sendInvitation(lobbyName, FirebaseAuth.getInstance().getCurrentUser().getUid(), friendId.get(which));
                                                Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Invitation sent to " + friendUsername.get(which), Snackbar.LENGTH_SHORT);
                                                mySnackbar.show();
                                            }
                                        });
                                    }
                                }
                                builder.create().show();
                            }
                        });

                //Handle the click on the Cancel button
                builder.setNegativeButton(context.getString(R.string.cancel_en), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the AlertDialog
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}