package ch.epfl.sdp.healthplay.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class User {

    public static final String DATABASE_URL = "https://health-play-9e161-default-rtdb.europe-west1.firebasedatabase.app";
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();

    private String username;
    private int calorieCounter = 0;
    private int age;
    private int weight;
    private int healthPoint;

    public User() {} //Default constructor required for calls to DataSnapshot.getValue(User.class)

    public User(String username, int age, int weight) {
        this.username = username;
        this.age = 0;
        this.healthPoint = 0;
        this.weight = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getCalorieCounter() {
        return calorieCounter;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public static void writeNewUser(String userId, String name, int age, int weight) {
        User user = new User(name, age, weight);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public static void writeUsername(String userId, String name){
        mDatabase.child("users").child(userId).child("username").setValue(name);
    }

    public static void writeCalorie(String userId, int calorieCounter){
        mDatabase.child("users").child(userId).child("calorieCounter").setValue(calorieCounter);
    }

    public static void writeAge(String userId, int age){
        mDatabase.child("users").child(userId).child("age").setValue(age);
    }

    public static void writeHealthPoint(String userId, int healthPoint){
        mDatabase.child("users").child(userId).child("healthPoint").setValue(healthPoint);
    }

    public static void writeWeight(String userId, int weight){
        mDatabase.child("users").child(userId).child("weight").setValue(weight);
    }

    public static void deleteUser(String userId) {
        mDatabase.child("users").child(userId).removeValue();
    }




}
