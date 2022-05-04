package ch.epfl.sdp.healthplay.planthunt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ch.epfl.sdp.healthplay.R;

public class PlanthuntWaitLobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_wait_lobby);


        final ImageView waitView = findViewById(R.id.planthuntWaitLobbyGif);
        Glide.with(this).load(R.drawable.loading_planthunt).into(waitView);



    }
}