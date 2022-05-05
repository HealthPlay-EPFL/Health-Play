package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.sdp.healthplay.GameMenuFragment;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntResultActivity extends AppCompatActivity {


    private final Database db = new Database();
    List<String> usernames = new ArrayList<>();
    List<Integer> scores = new ArrayList<>();
    TextView username1Text;
    TextView username2Text;
    TextView username3Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_result);

        Intent intent = getIntent();
        String lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);

        final TextView lobbyNameText = findViewById(R.id.planthuntResultName);
        lobbyNameText.setText(lobbyName);

        username1Text = findViewById(R.id.planthuntResultText1);
        username2Text = findViewById(R.id.planthuntResultText2);
        username3Text = findViewById(R.id.planthuntResultText3);

        //Get usernames from the database
        db.mDatabase.child(Database.LOBBIES).child(lobbyName).child(Database.MAX_NBR_PLAYERS)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot countSnapshot) {
                for (int i = 1; i < Math.toIntExact((long) countSnapshot.getValue()) + 1; i++) {
                    db.mDatabase
                            .child(Database.LOBBIES).child(lobbyName).child(Database.PLAYER_UID + i)
                            .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot uidSnapshot) {
                            usernames.add(Objects.requireNonNull(uidSnapshot.getValue()).toString());
                            System.out.println(usernames.toString());
                            if (usernames.size() == Math.toIntExact((long) countSnapshot.getValue()) && scores.size() == Math.toIntExact((long) countSnapshot.getValue())){
                                fillScores();
                            }
                        }
                    });
                }
            }
        });

        //Get scores from the database
        db.mDatabase.child(Database.LOBBIES).child(lobbyName).child(Database.MAX_NBR_PLAYERS)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot countSnapshot) {
                for (int i = 1; i < Math.toIntExact((long) countSnapshot.getValue()) + 1; i++) {
                    db.mDatabase
                            .child(Database.LOBBIES).child(lobbyName).child(Database.PLAYER_SCORE + i)
                            .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot uidSnapshot) {
                            scores.add(Math.toIntExact((long) uidSnapshot.getValue()));
                            System.out.println(scores.toString());
                            if (usernames.size() == Math.toIntExact((long) countSnapshot.getValue()) && scores.size() == Math.toIntExact((long) countSnapshot.getValue())){
                                fillScores();
                            }
                        }
                    });
                }
            }
        });



        Button leaveButton = findViewById(R.id.planthuntResultButton);

        //Start CreateLobby activity when clicking on Create button
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntResultActivity.this, GameMenuFragment.class);
                startActivity(intent);
            }
        });


    }

    private void fillScores(){
        if (usernames.size() < 3){
            usernames.add("");
            scores.add(0);
        }
        if (usernames.size() < 3){
            usernames.add("");
            scores.add(0);
        }

        int first = scores.get(0) > scores.get(1) && scores.get(0) > scores.get(2) ? 0 :
                (scores.get(1) > scores.get(0) && scores.get(1) > scores.get(2) ? 1 : 2);

        int third = scores.get(0) < scores.get(1) && scores.get(0) < scores.get(2) ? 0 :
                (scores.get(1) < scores.get(0) && scores.get(1) < scores.get(2) ? 1 : 2);

        int second = first != 0 && third != 0 ? 0 :
                (first != 1 && third != 1 ? 1 : 2);

        if(!usernames.get(first).equals("")){
            username1Text.setText(usernames.get(first) + "\n" + scores.get(first) + "points");
        }
        if(!usernames.get(second).equals("")){
            username2Text.setText(usernames.get(second) + "\n" + scores.get(second) + "points");
        }
        if(!usernames.get(third).equals("")){
            username3Text.setText(usernames.get(third) + "\n" + scores.get(third) + "points");
        }
    }

}