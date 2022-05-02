package ch.epfl.sdp.healthplay.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import ch.epfl.sdp.healthplay.EditProfilePictureFragment;
import ch.epfl.sdp.healthplay.ProfileSettingsFragment;
import ch.epfl.sdp.healthplay.QrCodeFragment;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.navigation.FragmentNavigation;

public class ProfileFragment extends Fragment {

    private Database db = new Database();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        SignedInFragment.SetMode(getContext());
        super.onCreate(savedInstanceState);
        initButton();
        getImage();
        FirebaseUser user = mAuth.getCurrentUser();

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
        return view;
    }

    private void getImage() {
        db.mDatabase.child(Database.USERS).child(mAuth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.getValue() != null){
                        String image = snapshot.getValue().toString();
                        ImageView imageView = view.findViewById(R.id.profile_picture);
                        Glide.with(getContext()).load(image).into(imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initButton(){
        Button statsButton = view.findViewById(R.id.statsButton);
        statsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setReorderingAllowed(true);
                        fragmentTransaction.replace(R.id.fragmentContainerView, new ProfileSettingsFragment());
                        fragmentTransaction.commit();
                    }
                }
        );
        view.findViewById(R.id.changeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.replace(R.id.fragmentContainerView, new EditProfilePictureFragment());
                fragmentTransaction.commit();
            }
        });

        FloatingActionButton goToQRCode = view.findViewById(R.id.goToQRCode);
        goToQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNavigation.switchToFragment(getParentFragmentManager(), new QrCodeFragment());
            }
        });
    }

    public void initBirthday(String userId) {
        TextView TextViewBirthday = view.findViewById(R.id.profileBirthday);

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
                try{
                    String[] parts = birthday.split("-");
                    birthday = parts[2] + "/" + parts[1] +"/" + parts[0];
                    TextViewBirthday.setText(birthday);
                }catch(Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });
    }




    public void initName(String userId) {
        TextView TextViewName = view.findViewById(R.id.profileName);


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
        TextView TextViewUsername = view.findViewById(R.id.profileUsername);

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
        TextView TextViewStatsButton = view.findViewById(R.id.statsButton);
        TextView TextViewWeight = view.findViewById(R.id.profileWeight);
        TextView TextViewHealthPoint = view.findViewById(R.id.profileHealthPoint);


        if (map!=null && map.containsKey(Database.getTodayDate())) {
            Map<String, Number> todayStats = map.get(Database.getTodayDate());

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
                    TextViewHealthPoint.setText("0");
                }
                if(todayStats.containsKey(Database.WEIGHT) &&
                        (todayStats.get(Database.WEIGHT)) != null) {
                    TextViewWeight.setText(String.valueOf(todayStats.get(Database.WEIGHT)));
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