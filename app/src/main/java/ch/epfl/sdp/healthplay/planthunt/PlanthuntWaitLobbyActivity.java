package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntWaitLobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_wait_lobby);


        final ImageView waitView = findViewById(R.id.planthuntWaitLobbyGif);
        Glide.with(this).load(R.drawable.loading_planthunt).into(waitView);

        //Initialize database reference
        Database db = new Database();

        Intent intent = getIntent();
        String lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        String currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);


        final TextView lobbyNameText = findViewById(R.id.planthuntWaitLobbyName);
        lobbyNameText.setText(lobbyName);

        List<String> usernames = new ArrayList<String>();
        final TextView username1Text = findViewById(R.id.planthuntWaitLobbyText1);
        final TextView username2Text = findViewById(R.id.planthuntWaitLobbyText2);
        final TextView username3Text = findViewById(R.id.planthuntWaitLobbyText3);

        db.getAllLobbyPlayerUids(lobbyName, task -> {
            if (!task.isSuccessful()) {

                Log.e("ERROR", "An error happened");
            }
            usernames.add(Objects.requireNonNull(task.getResult().getValue()).toString());
            if (usernames.size() == 3){
                username1Text.setText(usernames.get(0));
                username2Text.setText(usernames.get(1));
                username3Text.setText(usernames.get(2));
            }
        });

        Button readyButton = findViewById(R.id.planthuntWaitLobbyButton);

        //Create new lobby when clicking on Create lobby button
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntWaitLobbyActivity.this, PlanthuntLobbyActivity.class);
                intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
                intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, currentUsername);
                startActivity(intent);

            }
        });

    }
}