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
        Button lobbyButton = findViewById(R.id.joinLobbyButton);

        //Create new lobby when clicking on Create lobby button
        lobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Strings from input fields
                String name = getString(editName);
                String password = getString(editPassword);
                String username = getString(editUsername);

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

            }
        });
    }

    private String getString(EditText text){
        return text.getText().toString();
    }
}