package ch.epfl.sdp.healthplay;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        FragmentContainerView fragmentContainerView = (FragmentContainerView) findViewById(R.id.fragmentContainerView);
        //Set the navigation controller of the fragment container to the bottom navigation
        NavigationUI.setupWithNavController(bottomNavigationView, Navigation.findNavController(fragmentContainerView));

    }
}