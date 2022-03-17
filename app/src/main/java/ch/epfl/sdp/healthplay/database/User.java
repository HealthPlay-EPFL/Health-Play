package ch.epfl.sdp.healthplay.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class that represents a user in the database
 */
public final class User {

    private static final String DATABASE_URL = "https://health-play-9e161-default-rtdb.europe-west1.firebasedatabase.app";
    public static final String USERNAME = "username";
    public static final String CALORIE_COUNTER = "calorieCounter";
    public static final String AGE = "age";
    public static final String HEALTH_POINT = "healthPoint";
    public static final String WEIGHT = "weight";
    public static final String LAST_CURRENT_WEIGHT = "lastCurrentWeight";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String BIRTHDAY = "birthday";

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();

    private final static String STATS = "stats";
    private static final String USERS = "users";

    private String username;
    private String name;
    private String surname;
    private String email;
    // A yyyy-MM-dd formatted date
    private String birthday;
    private int age;
    private String lastCurrentWeight;

    // Format used to format date when adding stats
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public User() {
    } //Default constructor required for calls to DataSnapshot.getValue(User.class)

    public User(String username, String name, String surname, String email, String birthday, int age) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
        this.age = age;
        this.lastCurrentWeight = "";
    }

    /**
     * Creates a new user in the database
     *
     * @param userId   the unique identifier given to the user
     * @param userName the username
     * @param age      the age of the user
     * @param weight   the weight of the user
     */
    public static void writeNewUser(String userId, String userName, int age, int weight) {
        User user = new User(userName, "empty name", "empty surname", "empty@email.com", "2000-01-01", age);
        mDatabase.child(USERS).child(userId).setValue(user);
    }

    public static void writeUsername(String userId, String name) {
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
    public static void writeCalorie(String userId, int calorieCounter) {
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
    public static void addCalorie(String userId, int calories) {
        int toAdd = calories;
        Map<String, Map<String, String>> map = getStats(userId);
        // This bellow is to check the existence of the wanted calories
        // for today's date
        if (map.containsKey(getTodayDate())) {
            Map<String, String> calo = map.get(getTodayDate());
            String currentCalories;
            if (calo != null &&
                    calo.containsKey(CALORIE_COUNTER) &&
                    (currentCalories = calo.get(CALORIE_COUNTER)) != null) {
                toAdd += Integer.parseInt(currentCalories);
            }
        }
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(getTodayDate())
                .child(CALORIE_COUNTER)
                .setValue(toAdd);
    }

    public static void writeAge(String userId, int age) {
        mDatabase.child(USERS).child(userId).child(AGE).setValue(age);
    }

    public static void writeHealthPoint(String userId, int healthPoint) {
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(getTodayDate())
                .child(HEALTH_POINT)
                .setValue(healthPoint);
    }

    public static void writeWeight(String userId, double weight) {
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

    public static void writeName(String userId, String name) {
        mDatabase.child(USERS)
                .child(userId)
                .child(NAME)
                .setValue(name);
    }

    public static void writeSurname(String userId, String surname) {
        mDatabase.child(USERS)
                .child(userId)
                .child(SURNAME)
                .setValue(surname);
    }

    public static void writeBirthday(String userId, String birthday) {
        mDatabase.child(USERS)
                .child(userId)
                .child(SURNAME)
                .setValue(birthday);
        try {
            Date birth = format.parse(birthday);
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            if (birth != null){
                calendar.setTimeInMillis(today.getTime() - birth.getTime());
                int age  = calendar.get(Calendar.YEAR);
                writeAge(userId, age);
            }
        } catch (ParseException ignored) {
        }
    }

    public static void deleteUser(String userId) {
        mDatabase.child(USERS).child(userId).removeValue();
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getAge() {
        return age;
    }

    public String getLastCurrentWeight() {
        return lastCurrentWeight;
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
     * @return A map representing multiple maps of the given example.
     * If a user has no stats, this method will return an empty map.
     */
    public static Map<String, Map<String, String>> getStats(String userId) {
        Map<String, Map<String, String>> map = new HashMap<>();
        Task<DataSnapshot> t = mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .get()
                .addOnCompleteListener(task -> map.putAll(listenerTask(task)));

        try {
            // Wait for the listener to complete to get the result
            Tasks.await(t, 10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        return map;
    }

    // Helper function used inside the addOnCompleteListener of getStats method
    private static Map<String, Map<String, String>> listenerTask(Task<DataSnapshot> task) {
        if (!task.isSuccessful()) {
            Log.e("firebase", "Error getting data", task.getException());
        }

        try {
            // This SuppressWarnings is used because the data taken from
            // the database should be a JSON that is either empty or not
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> result =
                    (Map<String, Map<String, String>>) task.getResult().getValue();
            if (result != null) {
                return result;
            }
        } catch (ClassCastException ignored) {
            // If a cast exception is thrown, it is ignored and
            // the function will return an empty map
        }
        return new HashMap<>();
    }

    public static Task<DataSnapshot> readField(String userId, String field, OnCompleteListener<DataSnapshot> listener) {
        return User.mDatabase.child("users").child(userId).child(field).get().addOnCompleteListener(listener);
    }

    /**
     * Gets the current date in the yyyy-MM-dd format
     *
     * @return the current date in the yyyy-MM-dd format
     */
    @NonNull
    private static String getTodayDate() {
        return format.format(new Date());
    }

    /**
     * Gets the last entered weight for the given user if present
     *
     * @param userId the user id
     * @return the string of the last entered weight if the user ever entered one,
     * the empty string otherwise
     */
    public static String getLastEnteredWeight(String userId) {
        StringBuilder weight = new StringBuilder();
        mDatabase
                .child(USERS)
                .child(userId)
                .child(LAST_CURRENT_WEIGHT)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        weight.append(task.getResult());
                    }
                });
        return weight.toString();
    }
}
