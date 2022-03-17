package ch.epfl.sdp.healthplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

    private Map<String, Map<String, String>> userStats;

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
        ImageView imageView = (ImageView) view.findViewById(R.id.main_view);
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        TextView myDate = (TextView) view.findViewById(R.id.my_date);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*if (user != null) {
            userStats = User.getStats(user.getUid());
        }*/

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //String data = dayOfMonth + "/" + (month + 1) + "/" + year;
                String date = year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth);
                /*if(user != null){
                    myDate.setText("Calories: " + userStats.get(year + "-" + mFormat.format(month + 1) + "-" + mFormat.format(dayOfMonth)).get("caloriesCount"));
                }
                else{
                    myDate.setText(data);

                }*/
                myDate.setText(date);
            }
        });
        return view;
    }

}