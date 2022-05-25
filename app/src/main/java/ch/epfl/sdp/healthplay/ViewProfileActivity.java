package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.auth.SignedInFragment;


public class ViewProfileActivity extends ProfileActivity {

    public final static String MESSAGE = "id";
    /**
     * Profile of one of the top 5 player (cannot modify it).
     * Click on one of the LeaderBoard names, then click View Profile to access this view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SignedInFragment.SetMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Intent intent = getIntent();
        String userId = intent.getStringExtra(MESSAGE);
        ImageView imageView = findViewById(R.id.profile_picture);
        getImage(userId, imageView);
        TextView TextViewUsername = findViewById(R.id.profileUsername);
        initUsername(userId, TextViewUsername);
        TextView TextViewBirthday = findViewById(R.id.profileBirthday);
        initBirthday(userId, TextViewBirthday);
        TextView TextViewStatsButton = findViewById(R.id.statsButton);
        TextView TextViewWeight = findViewById(R.id.profileWeight);
        TextView TextViewHealthPoint = findViewById(R.id.profileHealthPoint);
        initStats(userId, TextViewStatsButton, TextViewWeight, TextViewHealthPoint);
        TextView TextViewName = findViewById(R.id.profileName);
        initName(userId, TextViewName);

    }
}