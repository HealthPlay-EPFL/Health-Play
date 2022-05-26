package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntJoinLobbyActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlanthuntJoinLobbyActivity.this, PlanthuntMainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_join_lobby);

        //Initialize database reference
        Database db = new Database();

        //Get name and password fields in the activity
        EditText editName = findViewById(R.id.planthuntJoinLobbyName);
        EditText editPassword = findViewById(R.id.planthuntJoinLobbyPassword);
        EditText editUsername = findViewById(R.id.planthuntJoinLobbyUsername);
        Button lobbyButton = findViewById(R.id.planthuntJoinLobbyButton);

        //Create new lobby when clicking on Create lobby button
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Strings from input fields
                String lobbyName = getString(editName);
                String password = getString(editPassword);
                String username = getString(editUsername);

                db.getLobbyPassword(lobbyName, task -> {
                    if (!task.isSuccessful()) {
                        Log.e("ERROR", "Lobby does not exist!");
                    }
                    if (Objects.requireNonNull(task.getResult().getValue()).toString().equals(password)){
                        db.getLobbyPlayerCount(lobbyName, Database.NBR_PLAYERS, task2 -> {
                            if (!task2.isSuccessful()) {
                                Log.e("ERROR", "Lobby does not exist!");
                            }
                            db.getLobbyPlayerCount(lobbyName, Database.MAX_NBR_PLAYERS, task3 -> {
                                if (!task3.isSuccessful()) {
                                    Log.e("ERROR", "Lobby does not exist!");
                                }
                                if (Math.toIntExact((long) task2.getResult().getValue()) < Math.toIntExact((long) task3.getResult().getValue())){
                                    int temp = db.addUserToLobby(lobbyName, username);
                                    handleJoinLobby(temp, lobbyName, username);

                                }
                                else{
                                    Log.e("ERROR", "Lobby is full!");
                                }
                            });
                        });
                    }
                });
            }
        });
    }

    private String getString(EditText text){
        return text.getText().toString();
    }

    protected void handleJoinLobby(int temp, String lobbyName, String username){
        if (temp == 0){
            //Launch lobby waiting screen
            Intent intent = new Intent(PlanthuntJoinLobbyActivity.this, PlanthuntWaitLobbyActivity.class);
            intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
            intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, username);
            intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, PlanthuntCreateJoinLobbyActivity.PLAYER);
            startActivity(intent);
        }
        else if (temp == 1){
            Snackbar.make(findViewById(R.id.planthuntJoinLobbyLayout), "Lobby does not exist", Snackbar.LENGTH_LONG).show();
        }
        else{
            Snackbar.make(findViewById(R.id.planthuntJoinLobbyLayout), "Lobby is full", Snackbar.LENGTH_LONG).show();
        }
    }

}

