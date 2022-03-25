package ch.epfl.sdp.healthplay;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.text.DecimalFormat;
import java.util.Map;

import ch.epfl.sdp.healthplay.database.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Home extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static Map<String, Map<String, String>> userStats;
    private static String test;
    private String selected_Date;

    private String mParam1;
    private String mParam2;
    private DecimalFormat mFormat= new DecimalFormat("00");

    public Frag_Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Home.
     */
    public static Frag_Home newInstance(String param1, String param2) {
        Frag_Home fragment = new Frag_Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__home, container, false);
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar);
        TextView dataDisplay = (TextView) view.findViewById(R.id.my_date);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //If a user is logged in, get his stats
        if(user != null) {
            User.getStats(user.getUid(), task -> {
                if (!task.isSuccessful()) {
                    Log.e("ERROR", "An error happened");
                }
                userStats = (Map<String, Map<String, String>>) task.getResult().getValue();

            });
        }
        //if he isn't
        else {
            userStats = null;
            FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(user != null) {
                        User.getStats(user.getUid(), task -> {
                            if (!task.isSuccessful()) {
                                Log.e("ERROR", "An error happened");
                            }
                            userStats = (Map<String, Map<String, String>>) task.getResult().getValue();
                            System.out.println("user stats: "+ userStats);
                        });
                        System.out.println("user stats: "+ userStats);
                    }
                }
            }

            );
        }
        String date = User.getTodayDate();
        //Print a text when the date is changed
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selected_Date = year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth);
                //No user
                if(user == null){
                    dataDisplay.setText("Please login");
                }
                //User but no data at all
                else if(userStats == null) {
                    dataDisplay.setText("No stats, please begin adding calories if you want to use the calendar summary");
                }
                //User with data, but no data for the chosen date
                else if(userStats.get(selected_Date) == null){
                    dataDisplay.setText("No data for this date");
                }
                else{
                    printStats(
                            dataDisplay,
                            selected_Date);
                }
            }
        });
        //Update in real time the userStats
        if(user != null) {
            User.mDatabase.child("users").child(user.getUid()).child("stats").child(date).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Get the changes
                    Map<String, String> value = (Map<String, String>) snapshot.getValue();
                    if (userStats != null) {
                        //Update all the values
                        System.out.println(value);
                        userStats.get(date).put(User.CALORIE_COUNTER, String.valueOf(value.get(User.CALORIE_COUNTER)));
                        userStats.get(date).put(User.HEALTH_POINT, String.valueOf(value.get(User.HEALTH_POINT)));
                        userStats.get(date).put(User.WEIGHT, String.valueOf(value.get(User.WEIGHT)));
                        //print the changes only if they happened on the focused date
                        if (selected_Date.equals(date)) {
                            printStats(
                                    dataDisplay,
                                    date);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println(error.toString());
                }
            }

            );
        }
        return view;
    }


    /**
     * Print the stats of the current user in the given textView, at the Date date
     * @param textView The textView you want to write in
     * @param date The date you want to write to
     */
    private void printStats(TextView textView, String date){
        if(userStats != null && FirebaseAuth.getInstance() != null) {
            textView.setText(
                    date + ": You've consumed :" +
                            "\n calories: " + String.valueOf(userStats.get(date).get(User.CALORIE_COUNTER)) +
                            "\n weight: " + String.valueOf(userStats.get(date).get(User.WEIGHT)) +
                            "\n health point: " + String.valueOf(userStats.get(date).get(User.HEALTH_POINT)));
        }
    }
}