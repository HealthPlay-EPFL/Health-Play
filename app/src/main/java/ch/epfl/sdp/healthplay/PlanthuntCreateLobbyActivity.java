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

    private static Button lobbyButton;
    private static final int TEST_1 = 180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_create_lobby);

        //Initialize database reference
        Database db = new Database();

        //Get name and password fields in the activity
        EditText editName = findViewById(R.id.createLobbyName);
        EditText editPassword = findViewById(R.id.createLobbyPassword);
        EditText editUsername = findViewById(R.id.createLobbyUsername);
        lobbyButton = findViewById(R.id.createLobbyButton);

        //Create new lobby when clicking on Create lobby button
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Strings from input fields
                String name = editName.getText().toString();
                String password = editPassword.getText().toString();
                String username = editUsername.getText().toString();

                //Initialize new lobby with received values
                Lobby newLobby = new Lobby(name, password, username, TEST_1);
                db.writeNewLobby(newLobby.getName(), newLobby.getPassword(), newLobby.getPlayerUid1(), newLobby.getRemainingTime());
            }
        });



    }
}