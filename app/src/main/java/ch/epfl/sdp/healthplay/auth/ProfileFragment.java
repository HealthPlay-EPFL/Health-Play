package ch.epfl.sdp.healthplay.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
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
import ch.epfl.sdp.healthplay.WelcomeScreenActivity;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.navigation.FragmentNavigation;
import ch.epfl.sdp.healthplay.productlist.ProductListFragment;

public class ProfileFragment extends Fragment {

    private Database db = new Database();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private View view;
    private String daily_calories;
    private int[] attrText = {R.attr.daily_calorie};
    private int[] style = {R.style.AppTheme, R.style.AppThemeFrench, R.style.AppThemeItalian, R.style.AppThemeGerman};

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
        initString();
        initButton();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            ImageView imageView = view.findViewById(R.id.profile_picture);
            getImage(user.getUid(), imageView);
            TextView textViewUsername = view.findViewById(R.id.profileUsername);
            initUsername(user.getUid(), textViewUsername);
            TextView textViewBirthday = view.findViewById(R.id.profileBirthday);
            initBirthday(user.getUid(), textViewBirthday);
            TextView textViewStatsButton = view.findViewById(R.id.statsButton);
            TextView textViewWeight = view.findViewById(R.id.profileWeight);
            TextView textViewHealthPoint = view.findViewById(R.id.profileHealthPoint);
            initStats(user.getUid(), textViewStatsButton, textViewWeight, textViewHealthPoint);
            TextView textViewName = view.findViewById(R.id.profileName);
            initName(user.getUid(), textViewName);
        }else {
            initProfile();
            view.findViewById(R.id.statsButton).setEnabled(false);
            view.findViewById(R.id.changeButton).setEnabled(false);
            view.findViewById(R.id.changeButton).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.goToProductList).setEnabled(false);
            view.findViewById(R.id.goToProductList).setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void initProfile(){
        //init the birthDay, get in the cache
        try {
            String birthday = WelcomeScreenActivity.cache.getField(Database.BIRTHDAY);
            String[] parts = birthday.split("-");
            birthday = parts[2] + "/" + parts[1] + "/" + parts[0];
            ((TextView) view.findViewById(R.id.profileBirthday)).setText(birthday);

            //init the name (name + surname), get in the cache
            String name = WelcomeScreenActivity.cache.getField(Database.NAME) + " " + WelcomeScreenActivity.cache.getField(Database.SURNAME);
            ((TextView) view.findViewById(R.id.profileName)).setText(name);

            //init the username, get in the cache
            String username = WelcomeScreenActivity.cache.getField(Database.USERNAME);
            ((TextView) view.findViewById(R.id.profileUsername)).setText(username);

            //init the healthPoint, Weight and Daily calorie, get in the cache
            Map<String, Map<String, Number>> stats = WelcomeScreenActivity.cache.getDataMap();
            updateStats(stats, "", ((TextView) view.findViewById(R.id.statsButton)), ((TextView) view.findViewById(R.id.profileWeight)), ((TextView) view.findViewById(R.id.profileHealthPoint)));

            //init the profile's image, get in the cache
            String image = WelcomeScreenActivity.cache.getField(Database.IMAGE);
            if (getActivity() != null) {
                Glide.with(getActivity()).load(image).into((ImageView) view.findViewById(R.id.profile_picture));
            }
        }catch (NullPointerException e){
            getActivity().startActivity(new Intent(getContext(), AuthUiActivity.class));
        }
    }

    public void getImage(String userId, ImageView imageView) {
        db.mDatabase.child(Database.USERS).child(userId).child(Database.IMAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.getValue() != null){
                        String image = snapshot.getValue().toString();
                        if(getActivity() != null)
                            Glide.with(getActivity()).load(image).into(imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Initiate the "onClick" property of the buttons of the view
     */
    private void initButton(){
        view.findViewById(R.id.statsButton).setEnabled(true);
        view.findViewById(R.id.changeButton).setEnabled(true);
        view.findViewById(R.id.changeButton).setVisibility(View.VISIBLE);
        view.findViewById(R.id.goToProductList).setEnabled(true);
        view.findViewById(R.id.goToProductList).setVisibility(View.VISIBLE);
        Button statsButton = view.findViewById(R.id.statsButton);
        //Go to the Profile Settings
        statsButton.setOnClickListener(FragmentNavigation.switchToFragmentListener(getParentFragmentManager(), new ProfileSettingsFragment()));
        // Go to the Edit Profile Picture Fragment
        view.findViewById(R.id.changeButton).setOnClickListener(FragmentNavigation.switchToFragmentListener(getParentFragmentManager(), new EditProfilePictureFragment()));
        // Go to the QRCode fragment
        view.findViewById(R.id.goToQRCode).setOnClickListener(FragmentNavigation.switchToFragmentListener(getParentFragmentManager(), new QrCodeFragment()));
        // Goto the product list view
        view.findViewById(R.id.goToProductList).setOnClickListener(FragmentNavigation.switchToFragmentListener(getParentFragmentManager(), new ProductListFragment()));
    }

    public void initBirthday(String userId, TextView TextViewBirthday) {

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




    public void initName(String userId, TextView TextViewName) {

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

    public void initUsername(String userId, TextView TextViewUsername) {
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

    public void initStats(String userId, TextView TextViewStatsButton,  TextView TextViewWeight, TextView TextViewHealthPoint) {

        db.getStats(userId,(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>)task.getResult().getValue();
                updateStats(map, userId, TextViewStatsButton, TextViewWeight, TextViewHealthPoint);
            }
        }));

        db.mDatabase.child(Database.USERS).child(userId).child(Database.STATS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, Number>> stats = (Map<String, Map<String, Number>>)dataSnapshot.getValue();
                if(stats != null) {
                    updateStats(stats,userId, TextViewStatsButton, TextViewWeight, TextViewHealthPoint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "Error:onCancelled", databaseError.toException());
            }
        });


    }


    public void updateStats(Map<String, Map<String, Number>> map, String userId, TextView TextViewStatsButton,  TextView TextViewWeight, TextView TextViewHealthPoint) {

        if (map!=null && map.containsKey(Database.getTodayDate())) {
            Map<String, Number> todayStats = map.get(Database.getTodayDate());

            if (todayStats != null){
                if(todayStats.containsKey(Database.CALORIE_COUNTER) &&
                        (todayStats.get(Database.CALORIE_COUNTER)) != null) {
                    String todayCalories = todayStats.get(Database.CALORIE_COUNTER) + "\n" + daily_calories;
                    TextViewStatsButton.setText(todayCalories);
                }
                else {
                    TextViewStatsButton.setText("0\n" + daily_calories);
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
            TextViewStatsButton.setText("0\n" + daily_calories);
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

    public void initString(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int language_mode = sharedPref.getInt(getString(R.string.saved_language_mode), 0);
        TypedArray t = getActivity().obtainStyledAttributes(style[language_mode], attrText);
        daily_calories = t.getString(0);
    }

}
