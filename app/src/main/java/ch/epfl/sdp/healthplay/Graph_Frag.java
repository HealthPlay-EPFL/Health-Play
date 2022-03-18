package ch.epfl.sdp.healthplay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Graph_Frag extends Fragment {

    private HashMap<String, BarData> monthData, weekData;
    private View view;
    private BarData draw;
    private String drawLabel;
    private BarChart bar;
    private static String[] categories = {"calories", "health points"};
    private static String[] weekLabel = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static String[] monthLabel = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public Graph_Frag() {
        // Required empty public constructor
    }

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

    private HashMap<String, float[]> initMap(String[] label){
        HashMap<String, float[]> map = new HashMap<>();
        map.put(categories[0], requireArguments().getFloatArray(label[0]));
        map.put(categories[1], requireArguments().getFloatArray(label[1]));
        return map;
    }

    public void initData(){
        String[] initWeekLabel = {"weekC", "weekH"};
        String[] initMonthLabel = {"monthC", "monthH"};
        String[] initLabel = {"Average of calories", "Sum of Health points"};
        weekData = initMapBar(initMap(initWeekLabel), initLabel);
        monthData = initMapBar(initMap(initMonthLabel), initLabel);

        draw = weekData.get(categories[0]);
        drawLabel = categories[0];
    }

    public void initBarChart(View view){
        bar = view.findViewById(R.id.idBarChart);
        bar.getDescription().setEnabled(false);
        bar.setFitBars(true);
        bar.animateY(1000);
        bar.setData(draw);
        setAxis(weekLabel, 10f);
    }

    public void setAxis(String[] valueFormatterX, float granularityY){
        XAxis xAxis = bar.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(valueFormatterX));

        YAxis axisLeft = bar.getAxisLeft();
        axisLeft.setGranularity(granularityY);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = bar.getAxisRight();
        axisRight.setGranularity(granularityY);
        axisRight.setAxisMinimum(0);
    }

    public void switchVisibilityOnButton(Button enable, Button notEnable){
        notEnable.setEnabled(false);
        notEnable.setVisibility(View.INVISIBLE);
        enable.setVisibility(View.VISIBLE);
        enable.setEnabled(true);
    }

    public void initButton(){
        Button prev = view.findViewById(R.id.buttonPrev);
        Button next = view.findViewById(R.id.buttonNext);
        Button buttonCal = view.findViewById(R.id.buttonCalories);
        Button buttonHealth = view.findViewById(R.id.buttonHealth);
        switchVisibilityOnButton(next, prev);
        switchVisibilityOnButton(buttonHealth, buttonCal);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnButton(1/*, prev, next, buttonCal, buttonHealth*/);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnButton(0/*, prev, next, buttonCal, buttonHealth*/);
            }
        });
        buttonCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnButton(2/*, prev, next, buttonCal, buttonHealth*/);
            }
        });
        buttonHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnButton(3/*, prev, next, buttonCal, buttonHealth*/);
            }
        });
    }

    public void clickOnButton(int button){
        Button prev = view.findViewById(R.id.buttonPrev);
        Button next = view.findViewById(R.id.buttonNext);
        Button buttonCal = view.findViewById(R.id.buttonCalories);
        Button buttonHealth = view.findViewById(R.id.buttonHealth);
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
        initData();
        initBarChart(view);
        return view;
    }

}