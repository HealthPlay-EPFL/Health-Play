package ch.epfl.sdp.healthplay;

import static ch.epfl.sdp.healthplay.database.User.listenerTask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.epfl.sdp.healthplay.database.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Home extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";private static Map<String, Map<String, String>> userStats;
    private static String test;

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

        if (user != null) {
            //userStats = User.getStats(user.getUid());
            
            readField("test","stats","2022-03-18");
        }
        else{
            System.out.println("WTFFFFFFFFFFFFFFFFF");
            FirebaseAuth.getInstance().signInWithEmailAndPassword("health.play@gmail.com","123456");
            readField("test","stats","2022-03-18");
            //readField("test", "stats","2022-03-18");
        }


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //String data = dayOfMonth + "/" + (month + 1) + "/" + year;
                String date = year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth);
                readField("test","stats",date);
                /*if(user != null){
                    myDate.setText("Calories: " + userStats.get(year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth)).get("caloriesCount"));
                }
                else{
                    myDate.setText(data);

                }*/
                if(test == null || test.equals("Please loggin")){
                    myDate.setText("Please loggin");
                }
                else{
                    int begin_calorie = test.indexOf("calorie_counter");
                    int begin_weight = test.indexOf("last_current_weight");
                    int begin_health = test.indexOf("health_point");
                    String[] cut = test.substring(1,test.length()-1).split(",");
                    myDate.setText(
                        /*date + ": You've consumed :" +
                        "\n calories: " + userStats.get(date).get("calories") +
                        "\n weight: " + userStats.get(date).get("last_current_weight") +
                        "\n health point" + userStats.get(date).get("health_point"));*/
                            date + ": \nStats :\n " +
                                    cut[0] +
                                    "\n" + cut[1] +
                                    "\n" + cut[2]);
                }
            }
        });
        return view;
    }
    public static String readField(String userId, String field, String date) {
        StringBuilder result = new StringBuilder();
        User.mDatabase.child("users").child(userId).child(field).child(date).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                if(task.getResult().getValue()==null){
                    test = "Please loggin";
                }
                else{
                    result.append(task.getResult().getValue().toString());
                    test = result.toString();
                }

            }
        });
        return "";
    }


}