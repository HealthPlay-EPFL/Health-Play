package ch.epfl.sdp.healthplay.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public final class Database {

    public static final String DATABASE_URL = "https://health-play-9e161-default-rtdb.europe-west1.firebasedatabase.app";
    public static final String USERNAME = "username";
    public static final String CALORIE_COUNTER = "calorieCounter";
    public static final String AGE = "age";
    public static final String HEALTH_POINT = "healthPoint";
    public static final String WEIGHT = "weight";
    public static final String LAST_CURRENT_WEIGHT = "lastCurrentWeight";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String BIRTHDAY = "birthday";

    public final DatabaseReference mDatabase;

    public static final String STATS = "stats";
    public static final String USERS = "users";

    // Format used to format date when adding stats
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public Database(DatabaseReference dbr) {
        mDatabase = dbr;
    }

    public Database() {
        mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();
    }
    /**
     * Creates a new user in the database
     *
     * @param userId   the unique identifier given to the user
     * @param userName the username
     * @param age      the age of the user
     * @param weight   the weight of the user
     */
    public void writeNewUser(String userId, String userName, int age, int weight) {
        mDatabase.child(USERS).child(userId).setValue( new User(userName, "empty name", "empty surname", "empty@email.com", "2000-01-01", age));
    }

    public void writeUsername(String userId, String name) {
        mDatabase
                .child(USERS)
                .child(userId)
                .child(USERNAME)
                .setValue(name);
    }

    /**
     * Writes the calorie counter for today and overwrites any
     * values that were present before.
     *
     * @param userId         the user ID
     * @param calorieCounter the number of calories
     */
    public void writeCalorie(String userId, int calorieCounter) {
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(getTodayDate())
                .child(CALORIE_COUNTER)
                .setValue(calorieCounter);
    }

    /**
     * Adds the given number of calories to the user's statistics. The
     * difference with {@linkplain #writeCalorie(String, int)} is that
     * this methods add to the current value contained for the day.
     *
     * @param userId   the user ID
     * @param calories the number of calories to add
     */
    public void addCalorie(String userId, int calories) {
        getStats(userId, task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            int toAdd = calories;
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) task.getResult().getValue();
            // This bellow is to check the existence of the wanted calories
            // for today's date
            if (map != null && map.containsKey(getTodayDate())) {
                Map<String, Number> calo = map.get(getTodayDate());
                long currentCalories;
                if (calo != null && calo.containsKey(CALORIE_COUNTER)) {
                    currentCalories = Long.parseLong(String.valueOf(calo.get(CALORIE_COUNTER)));
                    toAdd += currentCalories;
                }
            }
            mDatabase.child(USERS)
                    .child(userId)
                    .child(STATS)
                    .child(getTodayDate())
                    .child(CALORIE_COUNTER)
                    .setValue(toAdd);
        });
    }

    /**
     * Adds the given number of calories to the user's statistics. The
     * difference with {@linkplain #writeCalorie(String, int)} is that
     * this methods add to the current value contained for the day.
     *
     * @param userId   the user ID
     * @param healthPoint the number of calories to add
     */
    public void addHealthPoint(String userId, int healthPoint) {
        getStats(userId, task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            int toAdd = healthPoint;
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) task.getResult().getValue();
            // This bellow is to check the existence of the wanted calories
            // for today's date
            if (map != null && map.containsKey(getTodayDate())) {
                Map<String, Number> calo = map.get(getTodayDate());
                long currentHealthPoint;
                if (calo != null && calo.containsKey(HEALTH_POINT)) {
                    currentHealthPoint = Long.parseLong(String.valueOf(calo.get(HEALTH_POINT)));
                    toAdd += currentHealthPoint;
                }
            }
            mDatabase.child(USERS)
                    .child(userId)
                    .child(STATS)
                    .child(getTodayDate())
                    .child(HEALTH_POINT)
                    .setValue(toAdd);
        });
    }

    public void writeAge(String userId, int age) {
        mDatabase.child(USERS).child(userId).child(AGE).setValue(age);
    }

    public void writeWeight(String userId, double weight) {
        mDatabase.child(USERS)
                .child(userId)
                .child(LAST_CURRENT_WEIGHT)
                .setValue(weight);

        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(getTodayDate())
                .child(WEIGHT)
                .setValue(weight);
    }

    public void writeName(String userId, String name) {
        mDatabase.child(USERS)
                .child(userId)
                .child(NAME)
                .setValue(name);
    }

    public void writeSurname(String userId, String surname) {
        mDatabase.child(USERS)
                .child(userId)
                .child(SURNAME)
                .setValue(surname);
    }

    public void writeBirthday(String userId, String birthday) {
        mDatabase.child(USERS)
                .child(userId)
                .child(BIRTHDAY)
                .setValue(birthday);
        try {
            Date birth = format.parse(birthday);
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            if (birth != null) {
                calendar.setTimeInMillis(today.getTime() - birth.getTime());
                int age = calendar.get(Calendar.YEAR);

                writeAge(userId, age);
            }
        } catch (ParseException ignored) {
        }
    }

    public void deleteUser(String userId) {
        mDatabase.child(USERS).child(userId).removeValue();
    }

    /**
     * Given a userId, return the map containing each dates in the
     * yyyy-MM-dd format, each of which contains a map of available
     * fields such as {@value CALORIE_COUNTER}, {@value WEIGHT} and
     * {@value HEALTH_POINT}. The data retrieved is of the following
     * JSON form :
     * <pre>
     * {@code
     * {
     *   "2022-03-04" : {
     *     "caloriesCount" : 123,
     *     "healthPoints"  : 123,
     *     "weight"        : 123
     *   },
     *   ...
     * }}
     * </pre>
     * The resulting map for the above example would be:<br>
     * {@code {"2022-03-04"={"caloriesCount"=123, "healthPoints"=123, "weight"=123}, ...}}
     *
     * @param userId the id of the user
     */
    public void getStats(String userId, OnCompleteListener<DataSnapshot> onCompleteListener) {
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void readField(String userId, String field, OnCompleteListener<DataSnapshot> listener) {
       mDatabase.child(Database.USERS).child(userId).child(field).get().addOnCompleteListener(listener);
    }

    /**
     * Gets the current date in the yyyy-MM-dd format
     *
     * @return the current date in the yyyy-MM-dd format
     */
    @NonNull
    public static String getTodayDate() {
        return format.format(new Date());
    }

}
