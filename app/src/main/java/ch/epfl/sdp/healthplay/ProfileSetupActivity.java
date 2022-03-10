package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ch.epfl.sdp.healthplay.database.User;

public class ProfileSetupActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "ch.epfl.sdp.healthplay.USERNAME";
    //public static final String EXTRA_AGE = "ch.epfl.sdp.healthplay.AGE";
    //public static final String EXTRA_WEIGHT = "ch.epfl.sdp.healthplay.WEIGHT";
    public static final String EXTRA_ID = "ch.epfl.sdp.healthplay.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, GreetingActivity.class);
        EditText editName = findViewById(R.id.ProfileName);
        EditText editAge = findViewById(R.id.ProfileAge);
        EditText editWeight= findViewById(R.id.ProfileWeight);
        EditText editId = findViewById(R.id.profileId);

        int age = Integer.parseInt(editAge.getText().toString());
        int weight = Integer.parseInt(editWeight.getText().toString());
        String username = editName.getText().toString();
        String id = editId.getText().toString();
        User.writeNewUser(id,username, age, weight);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_ID, id);
        startActivity(intent);
    }
}