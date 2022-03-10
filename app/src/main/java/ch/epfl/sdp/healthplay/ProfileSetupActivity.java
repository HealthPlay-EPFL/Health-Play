package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ch.epfl.sdp.healthplay.database.User;

public class ProfileSetupActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "ch.epfl.sdp.healthplay.USERNAME";
    public static final String EXTRA_ID = "ch.epfl.sdp.healthplay.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

    }

    public void setProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        EditText editName = findViewById(R.id.ProfileName);
        EditText editAge = findViewById(R.id.ProfileAge);
        EditText editWeight= findViewById(R.id.ProfileWeight);
        EditText editId = findViewById(R.id.ProfileId);
        int age;
        int weight;
        try{
            age = Integer.parseInt(editAge.getText().toString());
            weight = Integer.parseInt(editWeight.getText().toString());
        }catch (NumberFormatException e){
            age = 0;
            weight = 0;
        }

        String username = editName.getText().toString();
        String id = editId.getText().toString();
        User.writeNewUser(id,username, age, weight);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_ID, id);
        startActivity(intent);
    }
}