package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.User;
//import static ch.epfl.sdp.healthplay.database.Database.INSTANCE;

public class ProfileSettingsActivity extends AppCompatActivity {

    FirebaseUser user;

    private void modifyText(FirebaseUser user, int layoutId, String field) {
        Database db = new Database();
        db.readField(user.getUid(), field, task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "ERRRORORORO");
            }
            String answer;
            if (field.equals(Database.LAST_CURRENT_WEIGHT)) {
                answer = String.valueOf(task.getResult().getValue());
            } else {
                answer = task.getResult().getValue(String.class);
            }
            EditText name = findViewById(layoutId);
            if (field.equals(Database.BIRTHDAY)) {
                // Receives the date as format like 2022-03-17
                // Must reverse the order
                String[] date = answer.split("-");
                answer = date[2] + "/" + date[1] + "/" + date[0];
            }
            name.setHint(answer);
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        //getSupportActionBar().hide();

        //FirebaseAuth.getInstance().signInWithEmailAndPassword("health-play@admin.ch", "123456");
        user = FirebaseAuth.getInstance().getCurrentUser();
        //FirebaseAuth.getInstance().signOut();

        if (user != null) {
            // Set all the hints
            modifyText(user, R.id.modifyNameEditText, Database.NAME);
            modifyText(user, R.id.modifySurnameEditText, Database.SURNAME);
            modifyText(user, R.id.modifyUsernameEditText, Database.USERNAME);
            modifyText(user, R.id.modifyBirthDateEditText, Database.BIRTHDAY);
            modifyText(user, R.id.modifyWeightEditText, Database.LAST_CURRENT_WEIGHT);
        }
    }

    public void saveChanges(View view) {
        if (user != null) {
            Database db = new Database();
            String uid = user.getUid();
            EditText text = findViewById(R.id.modifyNameEditText);
            db.writeName(uid, text.getText().toString());

            text = findViewById(R.id.modifySurnameEditText);
            db.writeSurname(uid, text.getText().toString());

            text = findViewById(R.id.modifyUsernameEditText);
            db.writeUsername(uid, text.getText().toString());

            text = findViewById(R.id.modifyBirthDateEditText);
            String birthday = text.getText().toString();
            String[] date = birthday.split("/");
            db.writeBirthday(uid, date[2] + "-" + date[1] + "-" + date[0]);

            text = findViewById(R.id.modifyWeightEditText);
            db.writeWeight(uid, Double.parseDouble(text.getText().toString()));
        }
    }
}