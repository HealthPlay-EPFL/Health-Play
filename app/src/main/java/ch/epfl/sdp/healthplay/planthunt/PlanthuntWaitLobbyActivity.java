package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

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
        String name = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);


        final TextView lobbyName = findViewById(R.id.planthuntWaitLobbyName);
        lobbyName.setText(name);


        db.getAllLobbyPlayerUids(name, task -> {
            if (!task.isSuccessful()) {

                Log.e("ERROR", "An error happened");
            }
            System.out.println(task.getResult().getValue());
        });

    }
}