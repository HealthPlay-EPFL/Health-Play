package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntJoinLobbyActivity extends AppCompatActivity {

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
                String name = getString(editName);
                String password = getString(editPassword);
                String username = getString(editUsername);

                Task checkId = db.getLobbyPassword(name);
                checkId.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot passwordSnapshot) {
                        if (Objects.requireNonNull(passwordSnapshot.getValue()).toString().equals(password)){
                            Task checkMax = db.getLobbyMaxPlayerCount(name);
                            checkMax.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot maxSnapshot) {
                                    Task checkCount = db.getLobbyPlayerCount(name);
                                    checkCount.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot countSnapshot) {
                                            if (Math.toIntExact((long) Objects.requireNonNull(countSnapshot.getValue())) < Math.toIntExact((long) passwordSnapshot.getValue())){
                                                db.addUserToLobby(name, username);

                                                //Launch lobby waiting screen
                                                Intent intent = new Intent(PlanthuntJoinLobbyActivity.this, PlanthuntWaitLobbyActivity.class);
                                                intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, name);
                                                intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, username);
                                                intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST, "player");
                                                startActivity(intent);
                                            }
                                            else{
                                                //TODO actual pop ups
                                                System.out.println("Lobby is full!");
                                            }
                                        }
                                    });
                                }

                            });
                        }
                        else{
                            //TODO actual pop ups
                            System.out.println("Incorrect lobby id!");
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