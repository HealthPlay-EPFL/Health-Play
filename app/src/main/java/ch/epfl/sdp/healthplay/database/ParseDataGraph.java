package ch.epfl.sdp.healthplay.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParseDataGraph {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private float[] dataWeekCalories;
    private float[] dataMonthCalories;
    private float[] dataWeekHealth;
    private float[] dataMonthHealth;
    private int[] shiftIndex = {5,6,0,1,2,3,4,5};
    private String calorieCounter = Database.CALORIE_COUNTER;
    private String healthS = Database.HEALTH_POINT;

    public ParseDataGraph(Map<String, Map<String, Number>> map){
        initDefaultValue();
        parse(init(map));
    }

    /**
     * Init all values of the tables to 0
     */
    private void initDefaultValue(){
        float[] weekC = new float[7];
        float[] weekH = new float[7];
        float[] monthC = new float[12];
        float[] monthH = new float[12];
        for(int i = 0; i<7; i++){
            weekC[i] = 0;
            weekH[i] = 0;
            monthH[i] = 0;
            monthC[i] = 0;
        }
        for(int i = 7; i<monthC.length; i++){
            monthH[i] = 0;
            monthC[i] = 0;
        }
        dataMonthHealth = monthH;
        dataWeekCalories = weekC;
        dataWeekHealth = weekH;
        dataMonthCalories = monthC;
    }

    /**
     * Parse the initMap
     * @param dataMap key : Date in one format yyyy-mm-dd value : Map with key : category (calorie, health, etc) and value
     * @return Map key : Date (Object format) value : (valueOfCategory, valueOfHealth)
     */
    private Map<Date, List<Float>> init(Map<String, Map<String, Number>> dataMap){
        if(dataMap==null){
            return new HashMap<>();
        }
        if(dataMap.isEmpty())
            return new HashMap<>();
        Map<Date, List<Float>> mapData = new HashMap<>();
        for (String dateFormat : dataMap.keySet()) {
            Date date = null;
            try {
                date = format.parse(dateFormat);
                Map<String, Number> data = dataMap.get(dateFormat);
                float calories = 0;
                float health = 0;
                List<Float> dataParse = new ArrayList<>();
                Number calorie = data.get(calorieCounter);
                Number healthP = data.get(healthS);
                if(calorie!=null)
                    calories += calorie.floatValue();
                if(healthP != null)
                    health += healthP.floatValue();
                dataParse.add(calories);
                dataParse.add(health);
                mapData.put(date, dataParse);
            } catch (ParseException e) {
            }
        }
        return mapData;
    }

    /**
     * Read the init map to fill the attribute table
     * @param data map key : Date (Object format) value : (valueOfCategory, valueOfHealth)
     */
    private void parse(Map<Date, List<Float>> data){
        if(data.isEmpty())
            return;
        float[] weekC = new float[7];
        float[] weekH = new float[7];
        float[] monthC = new float[12];
        float[] monthH = new float[12];
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int yearCurrent = cal.get(Calendar.YEAR);
        int weekOfYearCurrent = cal.get(Calendar.WEEK_OF_YEAR);
        for(Date date : data.keySet()){
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);

            if(year == yearCurrent){
                int month = cal.get(Calendar.MONTH);
                monthC[month] += data.get(date).get(0);
                monthH[month] += data.get(date).get(1);
                int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);

                if(weekOfYear == weekOfYearCurrent){
                    int day = cal.get(Calendar.DAY_OF_WEEK);
                    weekC[shiftIndex[day]] = data.get(date).get(0);
                    weekH[shiftIndex[day]] = data.get(date).get(1);
                }
            }
        }

        float[] averageMonthC = averageMonth(monthC, isBissextile(yearCurrent));
        dataMonthCalories = averageMonthC;
        dataMonthHealth = monthH;
        dataWeekCalories = weekC;
        dataWeekHealth = weekH;
    }

    /**
     * Compute the average in the month for all values in the table
     * @param dataMonth table with each case corresponds to one month and it fill with the sum of all value by day in the month
     * @param isYearBissextile boolean to know if the februar month contains 29 days or 28
     * @return table float fill with the average in the month
     */
    public float[] averageMonth(float[] dataMonth, boolean isYearBissextile){
        float monthC[] = new float[12];
        for(int i = 0; i<7; i++){
            if(i%2==0){
                monthC[i] = dataMonth[i]/31;
            }else
                monthC[i] = dataMonth[i]/30;
        }
        if(isYearBissextile){
            monthC[1] = dataMonth[1]/29;
        }else
            monthC[1] = dataMonth[1]/28;
        for(int i = 7; i<12; i++){
            if(i%2==1){
                monthC[i] = dataMonth[i]/31;
            }else
                monthC[i] = dataMonth[i]/30;
        }
        return monthC;
    }

    /**
     *
     * @param year Int
     * @return If this year is a bissextile year then returns true, else return false
     */
    public boolean isBissextile(int year){
        if(year%400==0)
            return true;
        if(year%100==0)
            return false;
        if(year%4==0)
            return true;
        return false;
    }

    /**
     *
     * @return table of length 7 the calories eat by day
     */
    public float[] getDataWeekCalories(){
        return  dataWeekCalories;
    }

    /**
     *
     * @return table of length 12 the average of calories by month
     */
    public float[] getDataMonthCalories(){
        return  dataMonthCalories;
    }
    /**
     *
     * @return table of length 7 the health point win by day
     */
    public float[] getDataWeekHealth(){
        return  dataWeekHealth;
    }

    /**
     *
     * @return table of length 12 the sum of health point win in the month
     */
    public float[] getDataMonthHealth(){
        return  dataMonthHealth;
    }
}
