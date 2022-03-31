package ch.epfl.sdp.healthplay.auth;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import ch.epfl.sdp.healthplay.ProfileSettingsActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

//import static ch.epfl.sdp.healthplay.database.Database.INSTANCE;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int mode = sharedPref.getInt(getString(R.string.saved_night_mode), AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(mode);
        if(mode == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Database db = new Database();
        if(user != null) {
            db.mDatabase
                    .child(Database.USERS)
                    .child(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        if(!task.getResult().hasChildren()){
                            db.writeNewUser(user.getUid(),"HugoBoss", 0, 0);
                        }
                        initUsername(user.getUid());
                        initBirthday(user.getUid());
                        initStats(user.getUid());
                        initName(user.getUid());

                    });

        }
    }

    public void changeProfile(View view) {
        Intent intent = new Intent(this, ProfileSettingsActivity.class);
        startActivity(intent);
    }

    public void initBirthday(String userId) {
        TextView TextViewBirthday = findViewById(R.id.profileBirthday);
        Database db = new Database();
        db.readField(userId, Database.BIRTHDAY, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                TextViewBirthday.setText(String.valueOf(task.getResult().getValue()));
            }
        }));

        db.mDatabase.child(Database.USERS).child(userId).child(Database.BIRTHDAY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String birthday = dataSnapshot.getValue(String.class);
                if(birthday != null) {
                    String[] parts = birthday.split("-");
                    birthday = parts[2] + "/" + parts[1] +"/" + parts[0];
                    TextViewBirthday.setText(birthday);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });
    }




    public void initName(String userId) {
        TextView TextViewName = findViewById(R.id.profileName);
        Database db = new Database();

        db.readField(userId, Database.NAME, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                db.readField(userId, Database.SURNAME, (t -> {
                    if (!t.isSuccessful()) {
                        Log.e("firebase", "Error getting data", t.getException());
                    } else {
                        String name = task.getResult().getValue() + " " + t.getResult().getValue();
                        TextViewName.setText(name);
                    }
                }));
            }
        }));

        db.mDatabase.child(Database.USERS).child(userId).child(Database.NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                db.readField(userId, Database.SURNAME, (task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        String name = dataSnapshot.getValue(String.class) + " " + task.getResult().getValue(String.class);
                        TextViewName.setText(name);
                    }
                }));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });

        db.mDatabase.child(Database.USERS).child(userId).child(Database.SURNAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                db.readField(userId, Database.NAME, (task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        String name = task.getResult().getValue(String.class) + " " + dataSnapshot.getValue(String.class);
                        TextViewName.setText(name);
                    }
                }));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });



    }

    private void initUsername(String userId) {
        TextView TextViewUsername = findViewById(R.id.profileUsername);
        Database db = new Database();
        db.readField(userId, Database.USERNAME, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                TextViewUsername.setText(String.valueOf(task.getResult().getValue()));
            }
        }));

        db.mDatabase.child(Database.USERS).child(userId).child(Database.USERNAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                TextViewUsername.setText(username);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });



    }

    public void initStats(String userId) {

        Database db = new Database();

        db.getStats(userId,(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>)task.getResult().getValue();
                updateStats(map, userId);
            }
        }));

        db.mDatabase.child(Database.USERS).child(userId).child(Database.STATS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, Number>> stats = (Map<String, Map<String, Number>>)dataSnapshot.getValue();
                if(stats != null) {
                    updateStats(stats,userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });


    }


    public void updateStats(Map<String, Map<String, Number>> map, String userId) {
        TextView TextViewStatsButton = findViewById(R.id.statsButton);
        TextView TextViewWeight = findViewById(R.id.profileWeight);
        TextView TextViewHealthPoint = findViewById(R.id.profileHealthPoint);
        Database db = new Database();

        if (map!=null && map.containsKey(db.getTodayDate())) {
            Map<String, Number> todayStats = map.get(db.getTodayDate());

            if (todayStats != null){
                if(todayStats.containsKey(Database.CALORIE_COUNTER) &&
                        (todayStats.get(Database.CALORIE_COUNTER)) != null) {
                    String todayCalories = todayStats.get(Database.CALORIE_COUNTER) + "\ndaily calories";
                    TextViewStatsButton.setText(todayCalories);
                }
                else {
                    TextViewStatsButton.setText("0\ndaily calories");
                }
                if(todayStats.containsKey(Database.HEALTH_POINT) &&
                        (todayStats.get(Database.HEALTH_POINT)) != null) {
                    TextViewHealthPoint.setText(String.valueOf(todayStats.get(Database.HEALTH_POINT)));
                }
                else {
                    db.readField(userId, Database.LAST_CURRENT_WEIGHT, (task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            TextViewWeight.setText(String.valueOf(task.getResult().getValue()));
                        }
                    }));
                }
                if(todayStats.containsKey(Database.WEIGHT) &&
                        (todayStats.get(Database.WEIGHT)) != null) {
                    TextViewWeight.setText(String.valueOf(todayStats.get(Database.WEIGHT)));
                }
                else {
                    TextViewHealthPoint.setText("0");
                }
            }
        }
        else {
            TextViewStatsButton.setText("0\ndaily calories");
            db.readField(userId, Database.LAST_CURRENT_WEIGHT, (task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    TextViewWeight.setText(String.valueOf(task.getResult().getValue()));
                }
            }));
            TextViewHealthPoint.setText("0");
        }


    }

}