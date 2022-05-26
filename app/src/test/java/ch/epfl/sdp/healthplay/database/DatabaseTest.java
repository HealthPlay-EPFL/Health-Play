package ch.epfl.sdp.healthplay.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

import static ch.epfl.sdp.healthplay.database.Database.NUTRIMENTS;
import static ch.epfl.sdp.healthplay.database.Database.getTodayDate;

import ch.epfl.sdp.healthplay.model.Product;


public class DatabaseTest {

    AutoCloseable closeable;
    String userId = "1";
    String userId2 = "2";
    String userId3 = "3";
    Map<String, User> map;
    Map<String, String> stats;
    String currentCalories = "15";
    String currentWeight = "50";
    String currentHealthPoint = "35";
    User user = new User("w", "empty name", "empty surname", "empty@email.com", "2000-01-01", 10);
    Map<String, Map<String, Number>> addMap;
    String usernametest = "Hugo";


    @Mock
    Task<DataSnapshot> t1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DatabaseReference dbr;

    @Mock
    DataSnapshot ds;

    @Mock
    Task<Void> tlb;
    @Mock
    Task<DataSnapshot> t2;
    @Mock
    DataSnapshot ds2;
    @Mock
    Task<DataSnapshot> tUsername;
    @Mock
    DataSnapshot dsUsername;



    public Task<Void> putUsername(String userId, String username, Map<String, User> map) {

       User user = map.get(userId);
       if(user != null) {
           user.setUsername(username);
       }
        return null;
    }
    public Task<Void> putName(String userId, String name, Map<String, User> map) {

        User user = map.get(userId);
        if(user != null) {
            user.setName(name);
        }
        return null;
    }
    public Task<Void> putSurname(String userId, String surName, Map<String, User> map) {

        User user = map.get(userId);
        if(user != null) {
            user.setSurname(surName);
        }
        return null;
    }
    public Task<Void> putBirthDay(String userId, String birthDay, Map<String, User> map) {

        User user = map.get(userId);
        if(user != null) {
            user.setBirthday(birthDay);
        }
        return null;
    }
    public Task<Void> putAge(String userId, int age, Map<String, User> map) {

        User user = map.get(userId);
        if(user != null) {
            user.setAge(age);
        }
        return null;
    }
    public Task<Void> putWeight(String userId, int weight, Map<String, User> map) {

        User user = map.get(userId);
        if(user != null) {
            user.setLastCurrentWeight(weight);
        }
        return null;
    }
    public Task<Void> putWeightStats(String weight, Map<String, String> stats) {
        stats.put(Database.WEIGHT, weight);
        return null;
    }
    public Task<Void> putCaloriesStats(String calories, Map<String, String> stats) {
        stats.put(Database.CALORIE_COUNTER, calories);
        return null;
    }
    public Task<Void> putNutrimentsStats(String calories, Map<String, String> stats) {
        stats.put(Product.Nutriments.ALCOHOL.getName(), calories);
        return null;
    }
    public Task<Void> putHealthPointStats(String healthPoint, Map<String, String> stats) {
        stats.put(Database.HEALTH_POINT, healthPoint);
        return null;
    }

    public Task<DataSnapshot> putCalories() {
        stats.put(Database.CALORIE_COUNTER, "50");
        return null;
    }

    public Task<Void> putUser(String userId, User user, Map<String, User> map) {
        map.put(userId, user);
        return null;
    }

    public Task<Void> removeUser(String userId, Map<String, User> map) {
        map.remove(userId);
        return null;
    }
    public Task<Void> getTlb(String healthPoint, Map<String, String> stats) {
        stats.put(Database.HEALTH_POINT, healthPoint);
        return tlb;
    }

    @Before
    public void init() {
        Database db = new Database(dbr);

        closeable = MockitoAnnotations.openMocks(this);
        map = new HashMap<String, User>();
        stats = new HashMap<>();
        map.put(userId,new User("a", "b", "c","empty@email.com", "2000-01-01", 10));
        stats.put(userId, currentCalories);
        stats.put(userId, currentWeight);
        stats.put(userId, currentHealthPoint);


        when(dbr.child(Database.USERS).child(userId).child(Database.USERNAME).setValue("username"))
                .thenReturn(putUsername(userId, "username",map));
        when(dbr.child(Database.USERS).child(userId).child(Database.NAME).setValue("name"))
                .thenReturn(putName(userId, "name",map));
        when(dbr.child(Database.USERS).child(userId).child(Database.SURNAME).setValue("surname"))
                .thenReturn(putSurname(userId, "surname",map));
        when(dbr.child(Database.USERS).child(userId).child(Database.LAST_CURRENT_WEIGHT).setValue(60))
                .thenReturn(putWeight(userId, 60,map));
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Database.HEALTH_POINT).setValue(currentHealthPoint))
                .thenReturn(putHealthPointStats(currentHealthPoint, stats));
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Database.WEIGHT).setValue(60))
                .thenReturn(putWeightStats("60", stats));

        when(dbr.child(Database.USERS).child(userId2).setValue(user))
                .thenReturn(putUser(userId2,user, map));

    }

    @Test
    public void writeUsernameTest() {
        Database db = new Database(dbr);
        db.writeUsername(userId, "username");
        assertEquals("username", map.get(userId).getUsername());
    }

    @Test
    public void writeNameTest() {
        Database db = new Database(dbr);
        db.writeName(userId, "name");
        assertEquals("name", map.get(userId).getName());
    }

    @Test
    public void writeSurNameTest() {
        Database db = new Database(dbr);
        db.writeSurname(userId, "surname");
        assertEquals("surname", map.get(userId).getSurname());
    }

    @Test
    public void writeBirthdayTest() {
        when(dbr.child(Database.USERS).child(userId).child(Database.BIRTHDAY).setValue("2009-01-01"))
                .thenReturn(putBirthDay(userId, "2009-01-01",map));
        Database db = new Database(dbr);
        db.writeBirthday(userId, "2009-01-01");
        assertEquals("2009-01-01", map.get(userId).getBirthday());
    }

    @Test
    public void writeAgeTest() {
        when(dbr.child(Database.USERS).child(userId).child(Database.AGE).setValue(10))
                .thenReturn(putAge(userId, 10,map));
        Database db = new Database(dbr);
        db.writeAge(userId, 10);
        assertEquals(10, map.get(userId).getAge());
    }

    @Test
    public void writeWeightTest() {
        Database db = new Database(dbr);
        db.writeWeight(userId, 60);
        assertEquals(60, map.get(userId).getLastCurrentWeight());
        assertEquals("60", stats.get(Database.WEIGHT));
    }

    @Test
    public void writeCaloriesTest() {
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Database.CALORIE_COUNTER).setValue(currentCalories))
                .thenReturn(putCaloriesStats(currentCalories, stats));
        Database db = new Database(dbr);
        db.writeCalorie(userId, 15);
        assertEquals(currentCalories, stats.get(Database.CALORIE_COUNTER));
    }

    @Test
    public void addHealthPointTest() {

        addMap = new HashMap<String, Map<String, Number>>();
        Map<String, Number> map1 = new HashMap<>();
        map1.put(Database.CALORIE_COUNTER, 40);
        map1.put(Database.HEALTH_POINT, 40);
        HashMap<String,HashMap<String, HashMap<String, String>>> leaderBoard = new HashMap<>();
        HashMap<String, HashMap<String, String>> mapDate = new HashMap<>();
        HashMap<String, String> l = new HashMap<>();
        l.put(userId,"Hugo");
        mapDate.put("75hp", l);
        leaderBoard.put(Database.getTodayDate(), mapDate);
        addMap.put(Database.getTodayDate(), map1);
        when(tlb.isSuccessful()).thenReturn(true);
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Database.HEALTH_POINT).setValue(75))
                .thenReturn(getTlb("75", stats));
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).get()).thenReturn(t1);
        when(dbr.child(Database.LEADERBOARD_DAILY).get()).thenReturn(t2);
        when(dbr.child(Database.LEADERBOARD_DAILY).setValue("75")).thenReturn(null);
        when(t2.isSuccessful()).thenReturn(true);
        when(t2.getResult()).thenReturn(ds2);
        when(t1.isSuccessful()).thenReturn(true);
        when(t1.getResult()).thenReturn(ds);
        when(ds.getValue()).thenReturn(addMap);
        when(ds2.getValue()).thenReturn(leaderBoard);
        when(tUsername.isSuccessful()).thenReturn(true);
        when(dbr.child(Database.USERS).child(userId).child(Database.USERNAME).get()).thenReturn(tUsername);
        when(t1.getResult()).thenReturn(dsUsername);
        when(ds.getValue()).thenReturn("Hugo");
        Database db = new Database(dbr);
        db.addHealthPoint(userId, 35);
        assertEquals("75", stats.get(Database.HEALTH_POINT));
    }

    @Test
    public void addHealthPointTestNew() {

        addMap = new HashMap<String, Map<String, Number>>();
        Map<String, Number> map1 = new HashMap<>();
        map1.put(Database.CALORIE_COUNTER, 40);
        map1.put(Database.HEALTH_POINT, 40);
        HashMap<String,HashMap<String, HashMap<String, String>>> leaderBoard = new HashMap<>();
        addMap.put(Database.getTodayDate(), map1);
        when(tlb.isSuccessful()).thenReturn(true);
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Database.HEALTH_POINT).setValue(75))
                .thenReturn(getTlb("75", stats));
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).get()).thenReturn(t1);
        when(dbr.child(Database.LEADERBOARD_DAILY).get()).thenReturn(t2);
        when(dbr.child(Database.LEADERBOARD_DAILY).setValue("75")).thenReturn(null);
        when(t2.isSuccessful()).thenReturn(true);
        when(t2.getResult()).thenReturn(ds2);
        when(t1.isSuccessful()).thenReturn(true);
        when(t1.getResult()).thenReturn(ds);
        when(ds.getValue()).thenReturn(addMap);
        when(ds2.getValue()).thenReturn(leaderBoard);
        when(tUsername.isSuccessful()).thenReturn(true);
        when(dbr.child(Database.USERS).child(userId).child(Database.USERNAME).get()).thenReturn(tUsername);
        when(t1.getResult()).thenReturn(dsUsername);
        when(ds.getValue()).thenReturn("Hugo");
        Database db = new Database(dbr);
        db.addHealthPoint(userId, 35);
        assertEquals("75", stats.get(Database.HEALTH_POINT));
    }

    @Test
    public void addCaloriesTest() {

        addMap = new HashMap<String, Map<String, Number>>();
        Map<String, Number> map1 = new HashMap<>();
        map1.put(Database.CALORIE_COUNTER, 40);
        map1.put(Database.HEALTH_POINT, 40);
        addMap.put(Database.getTodayDate(), map1);

        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).get()).thenReturn(t1);
        when(t1.isSuccessful()).thenReturn(true);
        when(t1.getResult()).thenReturn(ds);
        when(ds.getValue()).thenReturn(addMap);
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Database.CALORIE_COUNTER).setValue(75))
                .thenReturn(putCaloriesStats("75", stats));
        Database db = new Database(dbr);
        db.addCalorie(userId, 35);
        assertEquals("75", stats.get(Database.CALORIE_COUNTER));
    }

    @Test
    public void addNutrimentsFieldTest() {

        addMap = new HashMap<String, Map<String, Number>>();
        Map<String, Number> map1 = new HashMap<>();
        map1.put(Database.CALORIE_COUNTER, 40);
        map1.put(Database.HEALTH_POINT, 40);
        map1.put(Product.Nutriments.ALCOHOL.getName(), 40);
        addMap.put(Database.getTodayDate(), map1);
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).get()).thenReturn(t1);
        when(t1.isSuccessful()).thenReturn(true);
        when(t1.getResult()).thenReturn(ds);
        when(ds.getValue()).thenReturn(addMap);
        when(dbr.child(Database.USERS).child(userId).child(Database.STATS).child(Product.Nutriments.ALCOHOL.getName()).setValue(75))
                .thenReturn(putNutrimentsStats("75", stats));
        Database db = new Database(dbr);
        db.addNutrimentField(userId, Product.Nutriments.ALCOHOL,35);
        assertEquals("75", stats.get(Product.Nutriments.ALCOHOL.getName()));
    }


    @Test
    public void readFieldTest() {

        when(tUsername.isSuccessful()).thenReturn(true);
        when(dbr.child(Database.USERS).child(userId).child(Database.USERNAME).get()).thenReturn(tUsername);
        when(t1.getResult()).thenReturn(dsUsername);
        when(ds.getValue()).thenReturn("Hugo");
        Database db = new Database(dbr);
        db.readField(userId, Database.USERNAME,task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
                usernametest = "Hugo";
        });
        assertEquals("Hugo", usernametest);
    }

    @Test
    public void writeNewUserTest() {

        Database db = new Database(dbr);
        db.writeNewUser(userId2, "w", 10, 60);
        assertEquals("w", map.get(userId2).getUsername());
    }

    @Test
    public void deleteUserTest() {
        Database db = new Database(dbr);
        db.writeNewUser(userId2, "w", 10, 60);
        assertEquals("w", map.get(userId2).getUsername());
        when(dbr.child(Database.USERS).child(userId2).removeValue())
                .thenReturn(removeUser(userId2, map));
        db.deleteUser(userId2);
        assertNull(map.get(userId2));

    }

    @Test
    public void defaultConstructorUserTest() {
        map.put(userId3, new User());
        assertNotNull(map.get(userId3));
    }

    @Test
    public void writeBirthdayParseTest() {
        when(dbr.child(Database.USERS).child(userId).child(Database.BIRTHDAY).setValue("a-01-01"))
                .thenReturn(putBirthDay(userId, "a-01-01",map));
        Database db = new Database(dbr);
        db.writeBirthday(userId, "a-01-01");
        assertEquals("a-01-01", map.get(userId).getBirthday());
        assertEquals(10, map.get(userId).getAge());
    }
    
}
