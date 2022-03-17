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
    private BarData draw;
    private String drawLabel;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_, container, false);

        Button prev = view.findViewById(R.id.buttonPrev);
        prev.setEnabled(false);
        prev.setVisibility(View.INVISIBLE);

        Button next = view.findViewById(R.id.buttonNext);

        Button buttonCal = view.findViewById(R.id.buttonCalories);
        buttonCal.setVisibility(View.INVISIBLE);
        buttonCal.setEnabled(false);

        Button buttonHealth = view.findViewById(R.id.buttonHealth);

        String[] initWeekLabel = {"weekC", "weekH"};
        String[] initMonthLabel = {"monthC", "monthH"};
        String[] initLabel = {"Average of calories", "Sum of Health points"};
        weekData = initMapBar(initMap(initWeekLabel), initLabel);
        monthData = initMapBar(initMap(initMonthLabel), initLabel);

        draw = weekData.get(categories[0]);
        drawLabel = categories[0];
        BarChart bar = view.findViewById(R.id.idBarChart);
        bar.getDescription().setEnabled(false);
        bar.setFitBars(true);
        bar.animateY(1000);
        bar.setData(draw);

        XAxis xAxis = bar.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabel));

        YAxis axisLeft = bar.getAxisLeft();
        axisLeft.setGranularity(10f);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = bar.getAxisRight();
        axisRight.setGranularity(10f);
        axisRight.setAxisMinimum(0);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw = monthData.get(drawLabel);
                bar.setData(draw);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabel));
                bar.notifyDataSetChanged();
                bar.invalidate();
                bar.animateY(1000);
                next.setVisibility(View.INVISIBLE);
                next.setEnabled(false);
                prev.setEnabled(true);
                prev.setVisibility(View.VISIBLE);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw = weekData.get(drawLabel);
                bar.setData(draw);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabel));
                bar.notifyDataSetChanged();
                bar.invalidate();
                bar.animateY(1000);
                prev.setVisibility(View.INVISIBLE);
                prev.setEnabled(false);
                next.setEnabled(true);
                next.setVisibility(View.VISIBLE);
            }
        });
        buttonCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw = weekData.get(categories[0]);
                drawLabel = categories[0];
                bar.setData(draw);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabel));
                bar.notifyDataSetChanged();
                bar.invalidate();
                bar.animateY(1000);
                prev.setVisibility(View.INVISIBLE);
                prev.setEnabled(false);
                next.setEnabled(true);
                next.setVisibility(View.VISIBLE);
                buttonCal.setEnabled(false);
                buttonCal.setVisibility(View.INVISIBLE);
                buttonHealth.setVisibility(View.VISIBLE);
                buttonHealth.setEnabled(true);
            }
        });
        buttonHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw = weekData.get(categories[1]);
                drawLabel = categories[1];
                bar.setData(draw);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabel));
                bar.notifyDataSetChanged();
                bar.invalidate();
                bar.animateY(1000);
                prev.setVisibility(View.INVISIBLE);
                prev.setEnabled(false);
                next.setEnabled(true);
                next.setVisibility(View.VISIBLE);
                buttonHealth.setEnabled(false);
                buttonHealth.setVisibility(View.INVISIBLE);
                buttonCal.setVisibility(View.VISIBLE);
                buttonCal.setEnabled(true);
            }
        });
        return view;
    }

}