package ch.epfl.sdp.healthplay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.sdp.healthplay.auth.SignedInFragment;
import ch.epfl.sdp.healthplay.database.Database;

public class HomeScreenActivity extends AppCompatActivity {

    private Database database;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignedInFragment.SetMode(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        FragmentContainerView fragmentContainerView = (FragmentContainerView) findViewById(R.id.fragmentContainerView);
        //Set the navigation controller of the fragment container to the bottom navigation
        NavigationUI.setupWithNavController(bottomNavigationView, Navigation.findNavController(fragmentContainerView));

        database = new Database();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * When we start the Activity
     */
    @Override
    protected void onStart() {
        if(user != null){
            Log.e("ON START CALL", user.getUid());
            database.setOnlineStatus("online");
        }
        super.onStart();
    }

    /**
     * When we leave the Activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        //Get time and set the onlineStatus to it
        if(user != null){
            String timestamp = String.valueOf(System.currentTimeMillis());
            Log.e("ON PAUSE CALL", timestamp);
            database.setOnlineStatus(timestamp);
        }
    }

    /**
     * When we get back to the Activity
     */
    @Override
    protected void onResume() {
        //The user is on the chat (with you or another user)
        if(user != null){
            Log.e("ON REESUME CALL", user.getUid());
            database.setOnlineStatus("online");
        }
        super.onResume();
    }
}