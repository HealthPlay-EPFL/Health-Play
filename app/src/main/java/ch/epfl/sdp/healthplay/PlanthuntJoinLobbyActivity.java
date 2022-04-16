package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Lobby;

public class PlanthuntJoinLobbyActivity extends AppCompatActivity {

    private FirebaseUser user;
    private static Button lobbyButton;
    private static final int TEST_1 = 180, TEST_2 = 123, TEST_3 = 789, TEST_4 = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_join_lobby);

        //Initialize database reference
        Database db = new Database();

        //Get name and password fields in the activity
        EditText editName = findViewById(R.id.joinLobbyName);
        EditText editPassword = findViewById(R.id.joinLobbyPassword);
        EditText editUsername = findViewById(R.id.joinLobbyUsername);
        lobbyButton = findViewById(R.id.joinLobbyButton);

        //Create new lobby when clicking on Create lobby button
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Strings from input fields
                String name = editName.getText().toString();
                String password = editPassword.getText().toString();
                String username = editUsername.getText().toString();

                Task checkId = db.checkLobbyId(name, password);
                checkId.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(password)){
                            System.out.println("Lobby id is correct!");
                            //Initialize new lobby with received values
                            db.addUserToLobby(name, username);
                        }
                    }
                });
                /*db.addUserToLobby(newLobby.getName(), newLobby.getNbrPlayers(), "testPlayer2");
                newLobby.addPlayer();
                db.updateLobbyTime(newLobby.getName(), TEST_2);
                db.updateLobbyPlayerScore(newLobby.getName(), uid, TEST_3);
                db.updateLobbyPlayerScore(newLobby.getName(), "whatever", TEST_4);*/
            }
        });
    }
}