package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Lobby;

public class PlanthuntCreateLobbyActivity extends AppCompatActivity {


    private FirebaseUser user;
    private static Button lobbyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_create_lobby);

        Database db = new Database();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        EditText editName = findViewById(R.id.createLobbyName);
        EditText editPassword = findViewById(R.id.createLobbyPassword);
        lobbyButton = findViewById(R.id.createLobbyButton);

        //Create new lobby when clicking on Create lobby button
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String password = editPassword.getText().toString();
                Lobby newLobby = new Lobby(name, password, uid, 180);
                System.out.println(newLobby.getName());
                System.out.println(newLobby.getPassword());
                db.writeNewLobby(newLobby.getName(), newLobby.getPassword(), newLobby.getPlayerUid_1(), newLobby.getRemainingTime());
                db.addUserToLobby(newLobby.getName(), newLobby.getNbrPlayers(), "testPlayer1");
                newLobby.addPlayer();
                db.addUserToLobby(newLobby.getName(), newLobby.getNbrPlayers(), "testPlayer2");
                newLobby.addPlayer();
                db.updateLobbyTime(newLobby.getName(), 123);
                db.updateLobbyPlayerScore(newLobby.getName(), uid, 789);
                db.updateLobbyPlayerScore(newLobby.getName(), "whatever", 456);
            }
        });



    }
}