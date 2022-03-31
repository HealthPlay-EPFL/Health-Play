package ch.epfl.sdp.healthplay;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.text.DecimalFormat;
import java.util.Map;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.model.Graph_Frag;
//import static ch.epfl.sdp.healthplay.database.Database.INSTANCE;

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
    private String selectedDate;
    private Database database = new Database();

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
        String date = Database.getTodayDate();
        Button button = view.findViewById(R.id.switchFragButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, new Graph_Frag());
                fragmentTransaction.commit();
            }
        }
        );


        //If a user is logged in, get his stats
        if(user != null) {
            database.getStats(user.getUid(), task -> {
                if (!task.isSuccessful()) {
                    Log.e("ERROR", "Watch out there was an error");
                }
                userStats = (Map<String, Map<String, String>>) task.getResult().getValue();

            });
        }
        //If the user isn't logged in yet, create userStats when it logs in
        else {
            userStats = null;
            FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    //Necessary since the function is executed once on the creation of the Fragment
                    if(user != null) {
                        database.getStats(user.getUid(), task -> {
                            if (!task.isSuccessful()) {
                                Log.e("ERROR", "An error happened");
                            }
                            userStats = (Map<String, Map<String, String>>) task.getResult().getValue();
                        });
                    }
                }
            }

            );
        }

        //Print a text when the date is changed
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth);
                //No user logged in
                if(user == null){
                    dataDisplay.setText("Please login");
                }
                //User is logged in but no data at all
                else if(userStats == null) {
                    dataDisplay.setText("No stats, please begin adding calories if you want to use the calendar summary");
                }
                //User logged in with data, but no data for the chosen date
                else if(userStats.get(selectedDate) == null){
                    dataDisplay.setText("No data for this date");
                }
                else{
                    printStats(
                            dataDisplay,
                            selectedDate);
                }
            }
        });
      
        //Update in real time the userStats
        if(user != null) {
            database.mDatabase.child("users").child(user.getUid()).child("stats").child(date).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Get the changes
                    Map<String, String> value = (Map<String, String>) snapshot.getValue();
                    if (userStats != null) {
                        //Update all the values
                        userStats.get(date).put(Database.CALORIE_COUNTER, String.valueOf(value.get(Database.CALORIE_COUNTER)));
                        userStats.get(date).put(Database.HEALTH_POINT, String.valueOf(value.get(Database.HEALTH_POINT)));
                        userStats.get(date).put(Database.WEIGHT, String.valueOf(value.get(Database.WEIGHT)));

                        //print the changes only if they happened on the focused date
                        if (selectedDate != null && selectedDate.equals(date)) {
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
                            "\n calories: " + String.valueOf(userStats.get(date).get(Database.CALORIE_COUNTER)) +
                            "\n weight: " + String.valueOf(userStats.get(date).get(Database.WEIGHT)) +
                            "\n health point: " + String.valueOf(userStats.get(date).get(Database.HEALTH_POINT)));
        }
    }

}