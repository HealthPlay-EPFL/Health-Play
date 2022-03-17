package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.healthplay.database.User;

public class ProfileSettingsActivity extends AppCompatActivity {

    FirebaseUser user;

    private void modifyText(FirebaseUser user, int layoutId, String field) {
        EditText name = findViewById(layoutId);
        name.setHint(User.readField(user.getUid(), field));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Set all the hints
            modifyText(user, R.id.modifyNameEditText, User.NAME);
            modifyText(user, R.id.modifySurnameEditText, User.SURNAME);
            modifyText(user, R.id.modifyUsernameEditText, User.USERNAME);
            modifyText(user, R.id.modifyBirthDateEditText, User.BIRTHDAY);
            modifyText(user, R.id.modifyWeightEditText, User.WEIGHT);
        }
    }

    public void saveChanges(View view) {
        if (user != null) {
            String uid = user.getUid();
            EditText text = findViewById(R.id.modifyNameEditText);
            User.writeName(uid, text.getText().toString());

            text = findViewById(R.id.modifySurnameEditText);
            User.writeSurname(uid, text.getText().toString());

            text = findViewById(R.id.modifyUsernameEditText);
            User.writeUsername(uid, text.getText().toString());

            text = findViewById(R.id.modifyBirthDateEditText);
            String birthday = text.getText().toString();
            String[] date = birthday.split("-");
            User.writeBirthday(uid, date[2] + "-" + date[1] + "-" + date[0]);

            text = findViewById(R.id.modifyUsernameEditText);
            User.writeWeight(uid, Double.parseDouble(text.getText().toString()));
        }
    }
}