package ch.epfl.sdp.healthplay;

import static androidx.navigation.ViewKt.findNavController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        BottomNavigationView bottomNavigationView =(BottomNavigationView) findViewById(R.id.bottomNavigationView);
        FragmentContainerView fragmentContainerView = (FragmentContainerView) findViewById(R.id.fragmentContainerView);
        //Set the navigation controller of the fragment container to the bottom navigation
        NavigationUI.setupWithNavController(bottomNavigationView, Navigation.findNavController(fragmentContainerView));

    }
}