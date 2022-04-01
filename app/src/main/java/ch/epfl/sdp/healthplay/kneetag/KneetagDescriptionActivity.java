package ch.epfl.sdp.healthplay.kneetag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import ch.epfl.sdp.healthplay.GamesMenuActivity;
import ch.epfl.sdp.healthplay.PlanthuntDescriptionActivity;
import ch.epfl.sdp.healthplay.R;


public class KneetagDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kneetag_description);
        findViewById(R.id.kneetag_start).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );


    }
}
