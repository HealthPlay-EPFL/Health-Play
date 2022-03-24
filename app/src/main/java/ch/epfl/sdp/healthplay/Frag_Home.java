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

import ch.epfl.sdp.healthplay.auth.SignedInActivity;
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
    private Map<String, Map<String, String>> userStats;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__home, container, false);
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar);
        TextView myDate = (TextView) view.findViewById(R.id.my_date);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        User.getStats(user.getUid(), task -> {
                    if (!task.isSuccessful()) {
                        Log.e("ERROR", "EREREREROOORORO");
                    }
                    //int toAdd = User.calories;
                userStats = (Map<String, Map<String, String>>) task.getResult().getValue();

                    // This bellow is to check the existence of the wanted calories
                    // for today's date
               /* (userStats != null && userStats.containsKey(User.getTodayDate())) {
                        Map<String, Number> calo = userStats.get(User.getTodayDate());
                        long currentCalories;
                    }*/
                });
        String date = User.getTodayDate();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selected_Date = year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth);
                if(user == null){
                    myDate.setText("Please login");
                }
                else if(userStats == null) {
                    myDate.setText("No stats, please begin adding calories if you want to use the calendar summary");
                }
                else if(userStats.get(selected_Date) == null){
                    myDate.setText("No data for this date");
                }
                else{
                    myDate.setText(
                            selected_Date + ": You've consumed :" +
                        "\n calories: " + String.valueOf(userStats.get(selected_Date).get("calorieCounter")) +
                        "\n weight: " + String.valueOf(userStats.get(selected_Date).get("last_current_weight")) +
                        "\n health point" + String.valueOf(userStats.get(selected_Date).get("health_point")));

                }
            }
        });

        User.mDatabase.child("users").child(user.getUid()).child("stats").child(date).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,String> value = (Map<String, String>) snapshot.getValue();
                System.out.println("value = " + value);

                /*if(value.equals("calorieCounter")){
                    //userStats.get(User.getTodayDate()).put("calorieCounter", );
                }*/
                //System.out.println(String.valueOf(value.get("calorieCounter")));
                System.out.println(userStats);
                if(userStats != null){
                    System.out.println(userStats.get(date));
                    System.out.println("selected_Date = " + selected_Date);
                    userStats.get(date).put("calorieCounter", String.valueOf(value.get("calorieCounter")));
                    if(selected_Date.equals(date)) {
                        myDate.setText(
                                date + ": You've consumed :" +
                                        "\n calories: " + String.valueOf(userStats.get(date).get("calorieCounter")) +
                                        "\n weight: " + String.valueOf(userStats.get(date).get("last_current_weight")) +
                                        "\n health point: " + String.valueOf(userStats.get(date).get("health_point")));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        }

        );
        return view;
    }


}