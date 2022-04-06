package ch.epfl.sdp.healthplay.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.sdp.healthplay.Frag_Home;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.ParseDataGraph;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link } Graph_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Graph_Frag extends Fragment {
    private HashMap<String, BarData> monthData, weekData;
    private View view;
    private BarData draw;
    private String drawLabel;
    private BarChart bar;
    private static String[] categories = {"calories", "health points"};
    private static String[] weekLabel = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static String[] monthLabel = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private Button prev, next, buttonCal, buttonHealth;

    public Graph_Frag() {
        // Required empty public constructor
    }

    /**
     * Transform the float value to BarData
     * @param data data to draw in the graphic, key : calorie, health, value : list point
     * @param labels weekLabel or MonthLabel
     * @return HashMap key : calorie, health value : BarPoint (BarData)
     */
    private HashMap<String, BarData> initMapBar(HashMap<String, float[]> data, String[] labels){
        HashMap<String, BarData> map = new HashMap<>();
        int count =0;
        for (String category : categories) {
            ArrayList<BarEntry> entries = new ArrayList<>();
            float[] inter = data.get(category);
            for (int i = 0; i<inter.length; i++) {
                entries.add(new BarEntry(i, inter[i]));
            }
            BarDataSet barDataSet = new BarDataSet(entries, labels[count]);
            count++;
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            //barDataSet.setStackLabels(weekLabel);
            BarData barData = new BarData(barDataSet);
            barData.setValueTextSize(12f);
            barData.setBarWidth(0.5f);
            map.put(category, barData);
        }
        return map;
    }

    /**
     * Read the map and parse it to float tables and init BarData and draw table
     * @param map map input
     */
    private void initData(Map<String, Map<String, Number>> map) {
        HashMap<String, float []> initWeekData = new HashMap<>();
        HashMap<String, float []> initMonthData = new HashMap<>();
        ParseDataGraph parse = new ParseDataGraph(map);
        float[] weekDataC = parse.getDataWeekCalories();
        float[] weekDataH = parse.getDataWeekHealth();
        float[] monthDataC = parse.getDataMonthCalories();
        float[] monthDataH = parse.getDataMonthHealth();
        initWeekData.put(categories[0], weekDataC);
        initWeekData.put(categories[1], weekDataH);
        initMonthData.put(categories[0], monthDataC);
        initMonthData.put(categories[1], monthDataH);
        String[] initLabel = {"Average of calories", "Sum of Health points"};
        weekData = initMapBar(initWeekData, initLabel);
        monthData = initMapBar(initMonthData, initLabel);

        draw = weekData.get(categories[0]);
        drawLabel = categories[0];
    }

    /**
     * Init the barGraph on the view
     * @param view of the frag
     */
    private void initBarChart(View view){
        bar = view.findViewById(R.id.idBarChart);
        bar.getDescription().setEnabled(false);
        bar.setFitBars(true);
        bar.animateY(1000);
        bar.setData(draw);
        setAxis(weekLabel, 10f);
    }

    /**
     * SetAxis of the graph
     * @param valueFormatterX
     * @param granularityY
     */
    private void setAxis(String[] valueFormatterX, float granularityY){
        XAxis xAxis = bar.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(valueFormatterX));

        YAxis axisLeft = bar.getAxisLeft();
        axisLeft.setGranularity(granularityY);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = bar.getAxisRight();
        axisRight.setGranularity(granularityY);
        axisRight.setAxisMinimum(0);
    }

    /**
     * Switch the button, one button is not enable and invisible and another button is enable and visible
     * @param enable button will be enable and visible
     * @param notEnable button will be not enable and invisible
     */
    private void switchVisibilityOnButton(Button enable, Button notEnable){
        notEnable.setEnabled(false);
        notEnable.setVisibility(View.INVISIBLE);
        enable.setVisibility(View.VISIBLE);
        enable.setEnabled(true);
    }

    /**
     * Init button on the view
     */
    private void initButton(){
        prev = view.findViewById(R.id.buttonPrev);
        next = view.findViewById(R.id.buttonNext);
        buttonCal = view.findViewById(R.id.buttonCalories);
        buttonHealth = view.findViewById(R.id.buttonHealth);
        switchVisibilityOnButton(next, prev);
        switchVisibilityOnButton(buttonHealth, buttonCal);
        next.setOnClickListener((v) -> clickOnButton(1));
        prev.setOnClickListener((v) -> clickOnButton(0));
        buttonCal.setOnClickListener((v) -> clickOnButton(2));
        buttonHealth.setOnClickListener((v) -> clickOnButton(3));
        Button button = view.findViewById(R.id.buttonSwap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, new Frag_Home());
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Decide which action to make if the button i is clicked
     * @param button index of the button was clicked
     */
    private void clickOnButton(int button){
        if(button == 2){
            drawLabel = categories[0];
            switchVisibilityOnButton(buttonHealth, buttonCal);
        }else if(button == 3){
            drawLabel = categories[1];
            switchVisibilityOnButton(buttonCal, buttonHealth);
        }
        if(button != 1){
            draw = weekData.get(drawLabel);
            bar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekLabel));
            switchVisibilityOnButton(next, prev);
        }else{
            draw = monthData.get(drawLabel);
            bar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(monthLabel));
            switchVisibilityOnButton(prev, next);
        }
        bar.setData(draw);
        bar.notifyDataSetChanged();
        bar.invalidate();
        bar.animateY(1000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_, container, false);
        initButton();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Map<String, Map<String, Number>> map = null;
            initData(map);
            initBarChart(view);
        }else {
            String userID = user.getUid();
            initGraph(userID);
            new Database().mDatabase.child("users").child(user.getUid()).child("stats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    initGraph(userID);
                    switchVisibilityOnButton(buttonHealth, buttonCal);
                    switchVisibilityOnButton(next, prev);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }

    /**
     * Read the database of the user and init the graph
     * @param ID userID of the user
     */
    private void initGraph(String ID){
        new Database().getStats(ID, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) task.getResult().getValue();
                initData(map);
                initBarChart(view);
            }
        }));
    }

}