package ch.epfl.sdp.healthplay;

import static ch.epfl.sdp.healthplay.database.User.writeCalorie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sdp.healthplay.database.User;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       init_Display();

    }

    public void incrCalories(View view) {
        Intent intent = getIntent();
        String id = intent.getStringExtra(ProfileSetupActivity.EXTRA_ID);
        User.mDatabase.child("users").child(id).child("calorieCounter").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                int counter = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                writeCalorie(id,counter + 1);
            }
        });

    }

    public void initCaloriCounter(String id) {

        TextView TextView2 = findViewById(R.id.CalorieCounter);

        User.mDatabase.child("users").child(id).child("calorieCounter").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                int calories = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                String showCalories = calories + " calories!";
                TextView2.setText(showCalories);
            }
        });
    }

    public void init_Display() {

        Intent intent = getIntent();
        String name = intent.getStringExtra(ProfileSetupActivity.EXTRA_USERNAME);
        String id = intent.getStringExtra(ProfileSetupActivity.EXTRA_ID);

        TextView textView = findViewById(R.id.ProfileGreeting);
        TextView TextView2 = findViewById(R.id.CalorieCounter);

        String greeting = "Hi " + name;
        textView.setText(greeting);

        User.mDatabase.child("users").child(id).child("calorieCounter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer calories = dataSnapshot.getValue(Integer.class);
                String showCalories = calories + " calories!";
                TextView2.setText(showCalories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });
    }
}