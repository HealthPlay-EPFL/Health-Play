package ch.epfl.sdp.healthplay.auth;

import static ch.epfl.sdp.healthplay.database.User.writeCalorie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import ch.epfl.sdp.healthplay.ProfileSetupActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.User;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            User.mDatabase
                    .child(User.USERS)
                    .child(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        if(!task.getResult().hasChild(user.getUid())){
                            User.writeNewUser(user.getUid(),"HugoBoss", 0, 0);
                        }
                        initUsername(user.getUid());
                        initBirthday(user.getUid());
                        initStats(user.getUid());
                        initName(user.getUid());

                    });

        }
    }

    public void setProfile(View view) {

    }

    public void initBirthday(String userId) {
        TextView TextViewBirthday = findViewById(R.id.profileBirthday);

        User.readField(userId, User.BIRTHDAY, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                TextViewBirthday.setText(String.valueOf(task.getResult().getValue()));
            }
        }));

        User.mDatabase.child(User.USERS).child(userId).child(User.BIRTHDAY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String birthday = dataSnapshot.getValue(String.class);
                String[] parts = birthday.split("-");
                birthday = parts[2] + "/" + parts[1] +"/" + parts[0];
                TextViewBirthday.setText(birthday);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });
    }




    public void initName(String userId) {
        TextView TextViewName = findViewById(R.id.profileName);


        User.readField(userId, User.NAME, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                User.readField(userId, User.SURNAME, (t -> {
                    if (!t.isSuccessful()) {
                        Log.e("firebase", "Error getting data", t.getException());
                    } else {
                        String name = task.getResult().getValue() + " " + t.getResult().getValue();
                        TextViewName.setText(name);
                    }
                }));
            }
        }));

        User.mDatabase.child(User.USERS).child(userId).child(User.NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User.readField(userId, User.SURNAME, (task -> {
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

        User.mDatabase.child(User.USERS).child(userId).child(User.SURNAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User.readField(userId, User.NAME, (task -> {
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

        User.readField(userId, User.USERNAME, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                TextViewUsername.setText(String.valueOf(task.getResult().getValue()));
            }
        }));

        User.mDatabase.child(User.USERS).child(userId).child(User.USERNAME).addValueEventListener(new ValueEventListener() {
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

        User.getStats(userId,(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>)task.getResult().getValue();
                updateStats(map, userId);
            }
        }));

        User.mDatabase.child(User.USERS).child(userId).child(User.STATS).addValueEventListener(new ValueEventListener() {
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
        if (map!=null && map.containsKey(User.getTodayDate())) {
            Map<String, Number> todayStats = map.get(User.getTodayDate());

            if (todayStats != null){
                if(todayStats.containsKey(User.CALORIE_COUNTER) &&
                        (todayStats.get(User.CALORIE_COUNTER)) != null) {
                    String todayCalories = todayStats.get(User.CALORIE_COUNTER) + "\ndaily calories";
                    TextViewStatsButton.setText(todayCalories);
                }
                else {
                    TextViewStatsButton.setText("0\ndaily calories");
                }
                if(todayStats.containsKey(User.HEALTH_POINT) &&
                        (todayStats.get(User.HEALTH_POINT)) != null) {
                    TextViewHealthPoint.setText(String.valueOf(todayStats.get(User.HEALTH_POINT)));
                }
                else {
                    TextViewWeight.setText("0");
                }
                if(todayStats.containsKey(User.WEIGHT) &&
                        (todayStats.get(User.WEIGHT)) != null) {
                    TextViewWeight.setText(String.valueOf(todayStats.get(User.WEIGHT)));
                }
                else {
                    TextViewHealthPoint.setText("0");
                }
            }
        }
        else {
            TextViewStatsButton.setText("0\ndaily calories");
            TextViewWeight.setText("0");
            TextViewHealthPoint.setText("0");
        }


    }

}