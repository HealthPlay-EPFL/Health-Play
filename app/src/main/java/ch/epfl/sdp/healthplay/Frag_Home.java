package ch.epfl.sdp.healthplay;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.friendlist.FriendList_Frag;
import ch.epfl.sdp.healthplay.model.Graph_Frag;


public class Frag_Home extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Map<String, Map<String, String>> userStats;
    private static String test;
    private String selectedDate;
    private Database database = new Database();

    private DecimalFormat mFormat= new DecimalFormat("00");
    private final int NUMBER_OF_STRING = 6;
    private String[] text = new String[NUMBER_OF_STRING];
    private int[] attrText = {R.attr.pls_login, R.attr.no_data, R.attr.no_stat, R.attr.text_part1, R.attr.text_part2, R.attr.text_part3};
    private int[] style = {R.style.AppTheme, R.style.AppThemeFrench, R.style.AppThemeItalian, R.style.AppThemeGerman};

    public Frag_Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initText();
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar);
        TextView dataDisplay = (TextView) view.findViewById(R.id.my_date);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String date = Database.getTodayDate();
        Button button = view.findViewById(R.id.switchFragButton);
        Button friendListButton = view.findViewById(R.id.FriendList_button);

        //Add the onClick action to change the Frag displayed
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, new Graph_Frag());
                fragmentTransaction.commit();
            }
        }
        );

        friendListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, new FriendList_Frag());
                fragmentTransaction.commit();
            }
        }
        );

        try {
            userStats = WelcomeScreenActivity.cache.getDataMapCalendar();
        }catch (Exception e){
            userStats=null;
        }

        //Print a text when the date is changed
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth);
                //No user logged in
                if(user == null){
                    dataDisplay.setText(text[0]);
                    userStats = WelcomeScreenActivity.cache.getDataMapCalendar();
                }
                //User is logged in but no data at all
                if(userStats == null) {
                    dataDisplay.setText(text[2]);
                }
                //User logged in with data, but no data for the chosen date
                else if(userStats.get(selectedDate) == null){
                    dataDisplay.setText(text[1]);
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
            userStats = WelcomeScreenActivity.cache.getDataMapCalendar();
            if(userStats != null) {
                if (selectedDate != null && selectedDate.equals(date)) {
                    printStats(
                            dataDisplay,
                            date);
                }
            }
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
                    date + text[3] +
                            String.valueOf(userStats.get(date).get(Database.CALORIE_COUNTER)) +
                            text[4] + String.valueOf(userStats.get(date).get(Database.WEIGHT)) +
                            text[5] + String.valueOf(userStats.get(date).get(Database.HEALTH_POINT)));
        }
    }

    private void initText(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int language_mode = sharedPref.getInt(getString(R.string.saved_language_mode), 0);
        TypedArray t = getActivity().obtainStyledAttributes(style[language_mode], attrText);
        for(int i=0; i<text.length; i++){
            text[i] = t.getString(i);
        }
    }

}
