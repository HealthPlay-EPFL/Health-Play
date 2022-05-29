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
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class that represents a user in the database
 */
public final class User {

    private String username;
    private String name;
    private String surname;
    private String email;
    // A yyyy-MM-dd formatted date
    private String birthday;
    private int age;

    private Number lastCurrentWeight;


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
        this.lastCurrentWeight = 0;
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


    public Number getLastCurrentWeight() {
        return lastCurrentWeight;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setLastCurrentWeight(Number lastCurrentWeight) {
        this.lastCurrentWeight = lastCurrentWeight;
    }
}
