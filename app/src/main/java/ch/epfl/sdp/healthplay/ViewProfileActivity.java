package ch.epfl.sdp.healthplay;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import ch.epfl.sdp.healthplay.EditProfilePictureActivity;
import ch.epfl.sdp.healthplay.ProfileSettingsActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.auth.SignedInFragment;
import ch.epfl.sdp.healthplay.database.Database;


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