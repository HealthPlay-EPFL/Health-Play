package ch.epfl.sdp.healthplay.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ch.epfl.sdp.healthplay.model.Product;

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

    //Lobby related constants
    public static final String NBR_PLAYERS = "nbrPlayers";
    public static final String MAX_NBR_PLAYERS = "maxNbrPlayers";
    public static final String REMAINING_TIME = "remainingTime";
    public static final String PLAYER_UID = "playerUid";
    public static final String PLAYER_SCORE = "playerScore";
    public static final String PLAYER_READY = "playerReady";
    public static final String PLAYERS_READY = "playersReady";
    public static final String PLAYERS_GONE = "playersGone";
    public static final String PASSWORD = "password";

    public static final int MAX_PLAYER_CAPACITY = 3;

    //Leaderboard related constants
    public static final String LEADERBOARD_MONTHLY = "monthlyLeaderBoard";
    public static final String LEADERBOARD_DAILY = "leaderBoard";
    public static final String NUTRIMENTS = "nutriments";
    public static final String FRIEND = "friends";
    public final DatabaseReference mDatabase;

    public static final String STATS = "stats";
    public static final String USERS = "users";
    public static final String LOBBIES = "lobbies";
    public final static int SUFFIX_LEN = 2;
    private final static String SUFFIX = "hp";


    public static Comparator<String> comparator = (o1, o2) -> Long.compare(Long.parseLong(o2.substring(0,o2.length() - SUFFIX_LEN)), Long.parseLong(o1.substring(0,o1.length() - SUFFIX_LEN)));

    // Format used to format date when adding stats
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static final SimpleDateFormat formatYearMonth = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);

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
        Map<String, Object> chatStatus = new HashMap<>();
        chatStatus.put("onlineStatus", "offline");
        chatStatus.put("typingTo", "notTyping");
        mDatabase.child(USERS).child(userId).updateChildren(chatStatus);

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

    public void addNutrimentField(String userId, Product.Nutriments nutriment, double value) {
        getStats(userId, getLambda(userId, value, nutriment.getName()));
    }

    /**
     * Adds the given number of healthPoints to the user's statistics.
     * This methods add to the current value contained for the day, it also
     * update the leaderBoards if the new amount of HealthPoint is more than
     * what the current top five of players have
     *
     * @param userId the user ID
     * @param healthPoint the number of healthPoints to add
     */
    public void addHealthPoint(String userId, int healthPoint) {
        getStats(userId, getLambda(userId, healthPoint, HEALTH_POINT));
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

    public void initStatToDay(String userId){
        addCalorie(userId, 0);
        addHealthPoint(userId, 0);
        readField(userId, LAST_CURRENT_WEIGHT, (task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                mDatabase.child(USERS).child(userId).child(STATS).child(getTodayDate()).child(WEIGHT).setValue(task.getResult().getValue());
            }
        }));
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

    /**
     * Gets the current date in the specified format
     *
     * @return the current date in the specified format
     */

    @NonNull
    public static String getTodayDate(SimpleDateFormat format) {
        return format.format(new Date());
    }

    private OnCompleteListener<DataSnapshot> getLambda(String userId, double inc, String field) {
        return task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            double toAdd = inc;
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) task.getResult().getValue();
            // This bellow is to check the existence of the wanted calories
            // for today's date
            double currentCalories = 0;
            if (map != null && map.containsKey(getTodayDate())) {
                Map<String, Number> calo = map.get(getTodayDate());
                if (calo != null && calo.containsKey(field)) {
                    currentCalories = Double.parseDouble(String.valueOf(calo.get(field)));
                    toAdd += currentCalories;
                }
            }
            double monthlyHp = inc;
            double currentHp = 0;
            if (map != null && map.containsKey(getTodayDate(formatYearMonth))) {
                Map<String, Number> calo = map.get(getTodayDate(formatYearMonth));
                if (calo != null && calo.containsKey(field)) {
                    currentHp += Double.parseDouble(String.valueOf(calo.get(field)));
                    monthlyHp += currentHp;

                }

            }
            mDatabase.child(USERS)
                    .child(userId)
                    .child(STATS)
                    .child(getTodayDate())
                    .child(field)
                    .setValue(toAdd)
                    .addOnCompleteListener(getLambdaMonth(userId, monthlyHp, field))
                    .addOnCompleteListener(getLambdaHpMonth(userId, inc))
                    .addOnCompleteListener(getLambdaHp(userId, inc));
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
                    .removeValue();
        }
    }





    /** Creates a new lobby in the database
     *
     * @param name          the unique identifier given to the lobby
     * @param password      the password required to join the lobby
     * @param hostUid       the unique identifier of the lobby host
     * @param remainingTime the time the game will last for
     * @param maxNbrPlayers the number of expected players in the lobby
     */
    public void writeNewLobby(String name, String password, String hostUid, int remainingTime, int maxNbrPlayers){
        mDatabase.child(LOBBIES).child(name).setValue(new Lobby(name, password, hostUid, remainingTime, maxNbrPlayers));
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
                .child(NBR_PLAYERS)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int nbrPlayers = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString()) + 1;
                mDatabase
                        .child(LOBBIES)
                        .child(name)
                        .child(NBR_PLAYERS)
                        .setValue(nbrPlayers);
                mDatabase
                        .child(LOBBIES)
                        .child(name)
                        .child(PLAYER_UID + (nbrPlayers))
                        .setValue(playerUid);
            }
        });
    }

    /** Gets player value from lobby
     *
     * @param name    the unique identifier given to the lobby
     * @param request the value we're getting
     */
    public Task getLobbyPlayerCount (String name, String request, OnCompleteListener<DataSnapshot> onCompleteListener){
        return mDatabase
                .child(LOBBIES)
                .child(name)
                .child(request)
                .get().addOnCompleteListener(onCompleteListener);
    }

    /** Gets lobby password
     *
     * @param name the unique identifier given to the lobby
     */
    public Task getLobbyPassword(String name, OnCompleteListener<DataSnapshot> onCompleteListener){
        return mDatabase
                .child(LOBBIES)
                .child(name)
                .child(PASSWORD)
                .get().addOnCompleteListener(onCompleteListener);
    }

    /**
     * Increases lobby's ready players count
     *
     * @param name       the unique identifier given to the lobby
     */
    public void addLobbyReadyPlayer(String name){
        mDatabase
                .child(LOBBIES)
                .child(name)
                .child(PLAYERS_READY)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int readyPlayers = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString()) + 1;
                mDatabase
                        .child(LOBBIES)
                        .child(name)
                        .child(PLAYERS_READY)
                        .setValue(readyPlayers);
            }
        });
    }

    /**
     * Sets a lobby user as ready
     *
     * @param name      the unique identifier given to the lobby
     * @param playerUid the unique identifier of the ready player
     */
    public void setLobbyPlayerReady (String name, String playerUid){
        for (int i = 1; i < MAX_PLAYER_CAPACITY + 1; i++) {
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
                                .child(PLAYER_READY + finalI)
                                .setValue(true);
                    }
                }
            });
        }
    }

    /** Gets uids of all players in the lobby
     *
     * @param name the unique identifier given to the lobby
     */
    public void getAllLobbyPlayerUids (String name, OnCompleteListener<DataSnapshot> onCompleteListener){
        for (int i = 1; i < MAX_PLAYER_CAPACITY + 1; i++) {
            mDatabase
                    .child(LOBBIES)
                    .child(name)
                    .child(PLAYER_UID + i)
                    .get().addOnCompleteListener(onCompleteListener);
        }
    }

    /** Gets scores of all players in the lobby
     *
     * @param name       the unique identifier given to the lobby
     */
    public void getAllLobbyPlayerScores (String name, OnCompleteListener<DataSnapshot> onCompleteListener){
        for (int i = 1; i < MAX_PLAYER_CAPACITY + 1; i++) {
            mDatabase
                    .child(LOBBIES)
                    .child(name)
                    .child(PLAYER_SCORE + i)
                    .get().addOnCompleteListener(onCompleteListener);
        }
    }

    /**
     * Updates remaining time in the lobby's game
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
        for (int i = 1; i < MAX_PLAYER_CAPACITY + 1; i++) {
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

    /** Gets the score of a player in the lobby
     *
     * @param name       the unique identifier given to the lobby
     * @param playerUid  the unique identifier of the scoring player
     */
    public void getLobbyPlayerScore (String name, String playerUid, OnCompleteListener<DataSnapshot> onCompleteListener){
        for (int i = 1; i < MAX_PLAYER_CAPACITY + 1; i++) {
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
                                .get()
                                .addOnCompleteListener(onCompleteListener);
                    }
                }
            });
        }
    }

    /**
     * Increases lobby's gone players count
     *
     * @param name the unique identifier given to the lobby
     */
    public void addLobbyGonePlayer(String name){
        mDatabase
                .child(LOBBIES)
                .child(name)
                .child(PLAYERS_GONE)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                int gonePlayers = Integer.parseInt(Objects.requireNonNull(dataSnapshot.getValue()).toString()) + 1;
                mDatabase
                        .child(LOBBIES)
                        .child(name)
                        .child(PLAYERS_GONE)
                        .setValue(gonePlayers);
            }
        });
    }





    /**
     * Get the friend list of the user
     * @return a map of String to Boolean
     */
    public Map<String, Boolean> getFriendList() {
        Map<String, Boolean> outputMap = new HashMap<>();
        readField(FirebaseAuth.getInstance().getCurrentUser().getUid(), "friends", new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue() != null) {
                    outputMap.putAll((Map<String, Boolean>) task.getResult().getValue());
                }
            }
        });
        return outputMap;
    }

    /**
     * Update the LeaderBoard
     * @param userId
     */
    private void updateLeaderBoard(String userId, int toRemove) {
        getStats(userId,getLambdaUpdate(userId,toRemove, format, LEADERBOARD_DAILY));
    }

    private void updateMLeaderBoard(String userId, int toRemove) {
        getStats(userId,getLambdaUpdate(userId,toRemove, formatYearMonth, LEADERBOARD_MONTHLY));
    }

    private OnCompleteListener<DataSnapshot> getLambdaUpdate(String userId, int toRemove, SimpleDateFormat pformat, String pleaderBoard) {
        return task -> {
            if (!task.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Number>> mapStats = (Map<String, Map<String, Number>>) task.getResult().getValue();

            if (mapStats != null && mapStats.containsKey(Database.getTodayDate(pformat))) {
                Map<String, Number> currentStats = mapStats.get(Database.getTodayDate(pformat));
                String hp;
                if (currentStats != null && currentStats.containsKey(Database.HEALTH_POINT)) {
                    hp = String.valueOf(currentStats.get(Database.HEALTH_POINT));
                    getLeaderBoard(t -> {
                        if (!t.isSuccessful()) {
                            Log.e("firebase", "Error getting data", t.getException());
                        } else {
                            @SuppressWarnings("unchecked")
                            HashMap<String,HashMap<String,HashMap<String, String>>> leaderBoard = (HashMap<String,HashMap<String, HashMap<String, String>>>)t.getResult().getValue();
                            readField(userId, Database.USERNAME, ta -> {
                                if(!t.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", t.getException());
                                }
                                else {
                                    String username = ta.getResult().getValue(String.class);
                                    if(leaderBoard != null && leaderBoard.containsKey(getTodayDate(pformat))) {
                                        HashMap<String, String> l = leaderBoard.get(getTodayDate(pformat)).containsKey(hp + SUFFIX) ? leaderBoard.get(getTodayDate(pformat)).get(hp + SUFFIX) : new HashMap<String, String>();
                                        String hpPre = (Long.parseLong(hp) - toRemove) + SUFFIX;
                                        HashMap<String, String> lPre = leaderBoard.get(getTodayDate(pformat)).containsKey(hpPre) ? leaderBoard.get(getTodayDate(pformat)).get(hpPre) : new HashMap<String, String>();
                                        lPre.remove(userId);
                                        l.put(userId,username);
                                        leaderBoard.get(getTodayDate(pformat)).put(hp + SUFFIX,l);
                                        mDatabase.child(pleaderBoard).setValue(leaderBoard);
                                    }
                                    else if(leaderBoard != null && !leaderBoard.containsKey(getTodayDate(pformat))) {
                                        HashMap<String, HashMap<String, String>> map = new HashMap<>();
                                        HashMap<String, String> l = new HashMap<>();
                                        l.put(userId,username);
                                        map.put(hp + SUFFIX, l);
                                        leaderBoard.put(getTodayDate(pformat), map);
                                        mDatabase.child(pleaderBoard).setValue(leaderBoard);
                                    }
                                }
                            });
                        }
                    }, pleaderBoard);
                }
            }
        };
    }

    private OnCompleteListener<Void> getLambdaHp(String userId, double inc) {
        return t -> {
            if (!t.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            updateLeaderBoard(userId, (int) inc);
        };
    }
    private OnCompleteListener<Void> getLambdaHpMonth(String userId, double inc) {
        return t -> {
            if (!t.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            updateMLeaderBoard(userId, (int) inc);
        };
    }
    private OnCompleteListener<Void> getLambdaMonth(String userId, double inc, String field) {
        return t -> {
            if (!t.isSuccessful()) {
                Log.e("ERROR", "EREREREROOORORO");
            }
            mDatabase.child(USERS)
                    .child(userId)
                    .child(STATS)
                    .child(getTodayDate(formatYearMonth))
                    .child(field)
                    .setValue(inc);
        };
    }

    private void getLeaderBoard(OnCompleteListener<DataSnapshot> onCompleteListener, String pleaderBoard) {
        mDatabase.child(pleaderBoard)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

}
