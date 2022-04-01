package ch.epfl.sdp.healthplay.database;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ParseDataGraphTest {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static Calendar calendar = Calendar.getInstance();
    private int[] shiftIndex = {5,6,0,1,2,3,4,5};
    private float delta = Float.MIN_VALUE;
    private String calorieCounter = Database.CALORIE_COUNTER;
    private String healthS = Database.HEALTH_POINT;
    @Test
    public void parseWithMapNull(){
        Map<String, Map<String, Number>> map = null;
        testWhenExpectedArrayIsZero(map);
    }
    public void testWhenExpectedArrayIsZero(Map<String, Map<String, Number>> map){
        ParseDataGraph parse = new ParseDataGraph(map);
        float[] weekC = {0,0,0,0,0,0,0};
        float[] weekH = {0,0,0,0,0,0,0};
        float[] monthC = {0,0,0,0,0,0,0,0,0,0,0,0};
        float[] monthH = {0,0,0,0,0,0,0,0,0,0,0,0};
        Assert.assertArrayEquals(weekC, parse.getDataWeekCalories(), delta);
        Assert.assertArrayEquals(weekH, parse.getDataWeekHealth(), delta);
        Assert.assertArrayEquals(monthC, parse.getDataMonthCalories(), delta);
        Assert.assertArrayEquals(monthH, parse.getDataMonthHealth(), delta);
    }
    @Test
    public void parseWithMapEmpty(){
        Map<String, Map<String, Number>> map = new HashMap<>();
        testWhenExpectedArrayIsZero(map);
    }
    @Test
    public void parseWithDateFormatInvalid(){
        Map<String, Map<String, Number>> map = new HashMap<>();
        Map<String, Number> inter = new HashMap<>();
        String date = "dd-mm-yyyy";
        map.put(date, inter);
        testWhenExpectedArrayIsZero(map);
    }
    @Test
    public void parseWithMapIncomplete(){
        Map<String, Map<String, Number>> map = new HashMap<>();
        Map<String, Number> inter = new HashMap<>();
        float expectedValue = 500;
        inter.put(calorieCounter, expectedValue);
        Date date = new Date();
        String dateFormat = format.format(new Date());
        map.put(dateFormat, inter);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        float expectedWeek[] = {0,0,0,0,0,0,0};
        expectedWeek[shiftIndex[day]] = expectedValue;
        ParseDataGraph parse = new ParseDataGraph(map);
        Assert.assertArrayEquals(expectedWeek, parse.getDataWeekCalories(), delta);
        inter.remove(calorieCounter);
        inter.put(healthS, expectedValue);
        map.remove(dateFormat);
        map.put(dateFormat, inter);
        parse = new ParseDataGraph(map);
        Assert.assertArrayEquals(expectedWeek, parse.getDataWeekHealth(), delta);
    }

    @Test
    public void parseWithMapFillYearNoCurrent(){
        Map<String, Map<String, Number>> map = new HashMap<>();
        Map<String, Number> inter = new HashMap<>();
        float expectedValue = 500;
        inter.put(calorieCounter, expectedValue);
        inter.put(healthS, expectedValue);
        Date date = new Date();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        String dateFormat = format.format(date).replaceAll("" + year, "" + (year-1));
        map.put(dateFormat, inter);
        testWhenExpectedArrayIsZero(map);
    }
    @Test
    public void parseWithMapFillYearCurrentButNotWeekCurrent(){
        Map<String, Map<String, Number>> map = new HashMap<>();
        Map<String, Number> inter = new HashMap<>();
        float expectedValue = 500;
        inter.put(calorieCounter, expectedValue);
        inter.put(healthS, expectedValue);
        Date date = new Date();
        calendar.setTime(new Date());
        String dateFormat = format.format(date).substring(0, 5) + "01-01";
        map.put(dateFormat, inter);
        float[] expectedMonthC = {expectedValue/31,0,0,0,0,0,0,0,0,0,0,0};
        float[] expectedMonthH = {expectedValue,0,0,0,0,0,0,0,0,0,0,0};
        float[] expectedWeek = {0,0,0,0,0,0,0};
        ParseDataGraph parse = new ParseDataGraph(map);
        Assert.assertArrayEquals(expectedMonthC, parse.getDataMonthCalories(), delta);
        Assert.assertArrayEquals(expectedMonthH, parse.getDataMonthHealth(), delta);
        Assert.assertArrayEquals(expectedWeek, parse.getDataWeekHealth(), delta);
        Assert.assertArrayEquals(expectedWeek, parse.getDataWeekCalories(), delta);
    }

    @Test
    public void parseWithMapFillYearCurrentAndWeekCurrent(){
        Map<String, Map<String, Number>> map = new HashMap<>();
        Map<String, Number> inter = new HashMap<>();
        float expectedValue = 500;
        inter.put(calorieCounter, expectedValue);
        inter.put(healthS, expectedValue);
        Date date = new Date();
        calendar.setTime(new Date());
        String dateFormat = format.format(date);
        map.put(dateFormat, inter);
        float[] expectedWeek = {0,0,0,0,0,0,0};
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        expectedWeek[shiftIndex[dayOfWeek]] = expectedValue;
        ParseDataGraph parse = new ParseDataGraph(map);
        Assert.assertArrayEquals(expectedWeek, parse.getDataWeekHealth(), delta);
        Assert.assertArrayEquals(expectedWeek, parse.getDataWeekCalories(), delta);
    }

    @Test
    public void averageMonthTest(){
        float monthWithoutAverage[] = {500f, 600f, 700f, 800f, 900f, 1000f, 1100f, 1200f, 1300f, 1400f, 1500f, 1600f};
        ParseDataGraph parse = new ParseDataGraph(null);
        float expected[] = {500f/31f,600f/28f,700f/31f,800f/30f,900f/31f,1000f/30f,1100f/31f,1200f/31f,1300f/30f,1400f/31f,1500f/30f,1600f/31f};
        float[] expectedBis = expected;
        expectedBis[1] = 600f/29f;
        Assert.assertArrayEquals(expected, parse.averageMonth(monthWithoutAverage, false), 1f);
        Assert.assertArrayEquals(expectedBis, parse.averageMonth(monthWithoutAverage, true), 1f);
    }

    @Test
    public void isYearBissextileTest(){
        ParseDataGraph parse = new ParseDataGraph(null);
        int year = 100*469;
        Assert.assertFalse(parse.isBissextile(year));
        year = 400*645456;
        Assert.assertTrue(parse.isBissextile(year));
        year = 4*5454802;
        Assert.assertTrue(parse.isBissextile(year));
        year = 574515;
        Assert.assertFalse(parse.isBissextile(year));
    }

    @Test
    public void parseTest() throws ParseException {
        Map<String, Map<String, Number>> map = new HashMap<>();
        String dates[] = {"2000-05-06", "2002-09-17", "-01-01", "-02-22", "-03-04", "-04-29", "-05-15", "-06-18", "-07-30", "-08-29", "-09-10", "-10-17", "-11-11", "-12-25", ""};
        float expectedWeekC[] = {0,0,0,0,0,0,0};
        float expectedWeekH[] = {0,0,0,0,0,0,0};
        float expectedMonthC[] = {0,0,0,0,0,0,0,0,0,0,0,0};
        float expectedMonthH[] = {0,0,0,0,0,0,0,0,0,0,0,0};
        Date date = new Date();
        calendar.setTime(date);
        int yearCurrent = calendar.get(Calendar.YEAR);
        int weekCurrent = calendar.get(Calendar.WEEK_OF_YEAR);
        dates[dates.length-1] = format.format(date).substring(4);
        for(int i = 2; i<dates.length; i++){
            dates[i] = yearCurrent + dates[i];
            float calorie = 10*i;
            float health = 20*i;
            calendar.setTime(format.parse(dates[i]));
            if(weekCurrent == calendar.get(Calendar.WEEK_OF_YEAR)){
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                expectedWeekC[shiftIndex[day]] += calorie;
                expectedWeekH[shiftIndex[day]] += health;
            }
            int month = calendar.get(Calendar.MONTH);
            expectedMonthC[month] += calorie;
            expectedMonthH[month] += health;
            Map<String, Number> inter = new HashMap<>();
            inter.put(calorieCounter, calorie);
            inter.put(healthS, health);
            map.put(dates[i], inter);
        }
        Map<String, Number> inter = new HashMap<>();
        float calorie = 10;
        float health = 20;
        inter.put(calorieCounter, calorie);
        inter.put(healthS, health);
        map.put(dates[0], inter);
        map.put(dates[1], inter);
        ParseDataGraph parse = new ParseDataGraph(map);
        expectedMonthC = parse.averageMonth(expectedMonthC, parse.isBissextile(yearCurrent));
        Assert.assertArrayEquals(expectedWeekC, parse.getDataWeekCalories(), delta);
        Assert.assertArrayEquals(expectedWeekH, parse.getDataWeekHealth(), delta);
        Assert.assertArrayEquals(expectedMonthC, parse.getDataMonthCalories(), delta);
        Assert.assertArrayEquals(expectedMonthH, parse.getDataMonthHealth(), delta);
    }
}
