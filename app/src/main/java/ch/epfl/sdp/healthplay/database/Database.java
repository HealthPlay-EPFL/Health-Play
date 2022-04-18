package ch.epfl.sdp.healthplay.database;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import ch.epfl.sdp.healthplay.LeaderBoardActivity;
import ch.epfl.sdp.healthplay.R;

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
    public static final String NBR_PLAYER = "nbrPlayers";
    public static final String REMAINING_TIME = "remainingTime";
    public static final String PLAYER_UID = "playerUid";
    public static final String PLAYER_SCORE = "playerScore";
    public static final String PASSWORD = "password";
    public static final String STATUS = "status";
    public static final String LEADERBOARD = "leaderBoard";
    public static final String LEADERBOARD_DATE = "leaderBoardDate";
    public static final int MAX_NBR_PLAYERS = 3;

    public final DatabaseReference mDatabase;

    public static final String STATS = "stats";
    public static final String USERS = "users";
    public static final String LOBBIES = "lobbies";

    public static Comparator<String> comparator = (o1, o2) -> Long.compare(Long.parseLong(o2), Long.parseLong(o1));

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
        getStats(userId, getLambda(userId, calories, CALORIE_COUNTER));
    }

    /**
     * Adds the given number of calories to the user's statistics. The
     * difference with {@linkplain #writeCalorie(String, int)} is that
     * this methods add to the current value contained for the day. Also
     * update the leaderBoard if the new amount of HealthPoint is more than
     * what the current top five of players have
     *
     * @param userId   the user ID
     * @param healthPoint the number of calories to add
     */
    public void addHealthPoint(String userId, int healthPoint) {
        getStats(userId, getLambda(userId, healthPoint, HEALTH_POINT));
        updateLeaderBoard(userId, healthPoint);
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

    private OnCompleteListener<DataSnapshot> getLambda(String userId, int inc, String field) {
        return task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            int toAdd = inc;
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) task.getResult().getValue();
            // This bellow is to check the existence of the wanted calories
            // for today's date
            if (map != null && map.containsKey(getTodayDate())) {
                Map<String, Number> calo = map.get(getTodayDate());
                long currentCalories;
                if (calo != null && calo.containsKey(field)) {
                    currentCalories = Long.parseLong(String.valueOf(calo.get(field)));
                    toAdd += currentCalories;
                }
            }
            mDatabase.child(USERS)
                    .child(userId)
                    .child(STATS)
                    .child(getTodayDate())
                    .child(field)
                    .setValue(toAdd);
        };

    }

    /**
     * Add the given friend to the Database
     * @param friendUserId
     */
    public void addToFriendList(String friendUserId) {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabase.child(USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("friends")
                    .child(friendUserId)
                    .setValue(true);
        }
    }

    /**
     * Remove the given friend from the Database
     * @param friendUserId
     */
    public void removeFromFriendList(String friendUserId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabase.child(Database.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("friends")
                    .child(friendUserId)
                    .setValue(false);
        }
    }

    /** Creates a new lobby in the database
     *
     * @param name          the unique identifier given to the lobby
     * @param password      the password required to join the lobby
     * @param hostUid       the unique identifier of the lobby host
     * @param remainingTime the time the game will last for
     */
    public void writeNewLobby (String name, String password, String hostUid, int remainingTime){
        mDatabase.child(LOBBIES).child(name).setValue(new Lobby(name, password, hostUid, remainingTime));
    }

    /**
     * Adds a user to the database lobby
     *
     * @param name       the unique identifier given to the lobby
     * @param playerUid  the unique identifier of the joining player
     */
    public void addUserToLobby(String name, String playerUid){
        mDatabase
                .child(LOBBIES)
                .child(name)
                .child(NBR_PLAYER)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int nbrPlayers = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString()) + 1;
                mDatabase
                        .child(LOBBIES)
                        .child(name)
                        .child(NBR_PLAYER)
                        .setValue(nbrPlayers);
                mDatabase
                        .child(LOBBIES)
                        .child(name)
                        .child(PLAYER_UID + (nbrPlayers))
                        .setValue(playerUid);
            }
        });
    }

    /**
     * Updates remaining in the database lobby's game
     *
     * @param name          the unique identifier given to the lobby
     * @param remainingTime the new remaining time in the game
     */
    public void updateLobbyTime (String name, int remainingTime){
        mDatabase
                .child(LOBBIES)
                .child(name)
                .child(REMAINING_TIME)
                .setValue(remainingTime);
    }

    /**
     * Updates the score of a player in the lobby
     *
     * @param name      the unique identifier given to the lobby
     * @param playerUid the unique identifier of the scoring player
     * @param score     the new score of the player
     */
    public void updateLobbyPlayerScore (String name, String playerUid, int score){
        for (int i = 1; i < MAX_NBR_PLAYERS + 1; i++) {
            int finalI = i;
            mDatabase
                    .child(LOBBIES)
                    .child(name)
                    .child(PLAYER_UID + i)
                    .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(playerUid)) {
                                mDatabase
                                        .child(LOBBIES)
                                        .child(name)
                                        .child(PLAYER_SCORE + finalI)
                                        .setValue(score);
                            }
                        }
                    });
        }
    }

    /**
     * Get the friend list of the user
     * @return a map of String to Boolean
     */
    public Map<String, Boolean> getFriendList(){
        Map<String, Boolean> outputMap = new HashMap<>();
        readField(FirebaseAuth.getInstance().getCurrentUser().getUid(), "friends", new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                outputMap.putAll((Map<String, Boolean>) task.getResult().getValue());
            }
        });
        return outputMap;

     * Checks if lobby exists and given password matches correct one
     *
     * @param name      the unique identifier given to the lobby
     * @param password  the unique password given to the lobby
     */
    public Task checkLobbyId (String name, String password){
        return mDatabase
                .child(LOBBIES)
                .child(name)
                .child(PASSWORD)
                .get();
    }

    private void updateLeaderBoard(String userId, int toRemove) {

        getStats(userId,getLambdaUpdate(userId, toRemove));

    }

    private OnCompleteListener<DataSnapshot> getLambdaUpdate(String userId, int toRemove) {
        return task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }

            @SuppressWarnings("unchecked")
            Map<String, Map<String, Number>> mapStats = (Map<String, Map<String, Number>>) task.getResult().getValue();

            if (mapStats != null && mapStats.containsKey(Database.getTodayDate())) {
                Map<String, Number> currentStats = mapStats.get(Database.getTodayDate());
                String hp;
                if (currentStats != null && currentStats.containsKey(Database.HEALTH_POINT)) {
                    hp = String.valueOf(currentStats.get(Database.HEALTH_POINT));

                    getLeaderBoard(t -> {
                        if (!t.isSuccessful()) {
                            Log.e("firebase", "Error getting data", t.getException());
                        } else {
                            @SuppressWarnings("unchecked")
                            HashMap<String,HashMap<String, ArrayList<String>>> leaderBoard = (HashMap<String,HashMap<String, ArrayList<String>>>)t.getResult().getValue();
                            if(leaderBoard != null && leaderBoard.containsKey(getTodayDate())) {

                                ArrayList<String> l = leaderBoard.get(getTodayDate()).containsKey(hp) ? leaderBoard.get(getTodayDate()).get(hp) : new ArrayList<>();
                                String hpPre = String.valueOf(Long.parseLong(hp) - toRemove);
                                ArrayList<String> lPre = leaderBoard.get(getTodayDate()).containsKey(hpPre) ? leaderBoard.get(getTodayDate()).get(hpPre) : new ArrayList<>();
                                lPre.remove(userId);
                                l.add(userId);
                                leaderBoard.get(getTodayDate()).put(hp,l);
                                mDatabase.child(LEADERBOARD).setValue(leaderBoard);
                            }
                            else if(leaderBoard != null) {
                                HashMap<String, ArrayList<String>> map = new HashMap<>();
                                ArrayList<String> l = new ArrayList<>();
                                l.add(userId);
                                map.put(hp, l);
                                leaderBoard = new HashMap<>();
                                leaderBoard.put(getTodayDate(), map);
                                mDatabase.child(LEADERBOARD).setValue(leaderBoard);

                            }

                        }


                    });

                }

            }
        };

    }

    private void getLeaderBoard(OnCompleteListener<DataSnapshot> onCompleteListener) {
        mDatabase.child(Database.LEADERBOARD)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
}
