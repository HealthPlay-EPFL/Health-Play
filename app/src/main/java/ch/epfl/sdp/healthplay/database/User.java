package ch.epfl.sdp.healthplay.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
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
    }

    public static void writeNewUser(String userId, String userName, int age, int weight) {
        User user = new User(userName, "empty name", "emtpy surname", "empty@email.com", "2000-01-01", age);
        mDatabase.child(USERS).child(userId).setValue(user);
    }

    public static void writeUsername(String userId, String name) {
        mDatabase.child(USERS).child(userId).child(USERNAME).setValue(name);
    }

    public static void writeCalorie(String userId, int calorieCounter) {
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(format.format(new Date()))
                .child(CALORIE_COUNTER)
                .setValue(calorieCounter);
    }

    public static void writeAge(String userId, int age) {
        mDatabase.child(USERS).child(userId).child(AGE).setValue(age);
    }

    public static void writeHealthPoint(String userId, int healthPoint) {
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(format.format(new Date()))
                .child(HEALTH_POINT)
                .setValue(healthPoint);
    }

    public static void writeWeight(String userId, int weight) {
        mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .child(format.format(new Date()))
                .child(WEIGHT)
                .setValue(weight);
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

    /**
     * Given a userId, return the map containing each dates in the
     * yyyy-MM-dd format, each of which contains a map of available
     * fields such as {@value CALORIE_COUNTER}, {@value WEIGHT} and
     * {@value HEALTH_POINT}.
     *
     * @param userId the id of the user
     * @return a map of a map
     */
    public static Map<String, Map<String, String>> getStats(String userId) {
        Map<String, Map<String, String>> map = new HashMap<>();
        Task<DataSnapshot> t = mDatabase.child(USERS)
                .child(userId)
                .child(STATS)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("test", String.valueOf(task.getResult().getValue()));
                        map.putAll((Map<String, Map<String, String>>) task.getResult().getValue());
                    }
                });

        try {
            Tasks.await(t, 10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        return map;
    }
}
