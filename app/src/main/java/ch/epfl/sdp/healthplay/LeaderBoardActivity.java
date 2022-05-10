package ch.epfl.sdp.healthplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntMainActivity;

public class LeaderBoardActivity extends AppCompatActivity{
    public final Database db = new Database();
    public final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public int MIN_RANK;
    public Button[] tab;
    public PopupMenu.OnMenuItemClickListener[] menus;
    public String[] ids;
    public PopupMenu.OnMenuItemClickListener[] myMenus;
    public ImageView[] images;
    public final static int STRING_MAX_LEN = 40;
    /**
     * Setup the leaderBoard representing the current top 5 of players based on healthPoint.
     * The leaderBoard is reset daily
     * If you are not in the current top 5 but you still earned points that day, your rank will be shown below the leaderBoard in parentheses
     * If you haven t earned any points that day, a message saying that you are unranked will appear
     * You can add any player of the top 5 (except you) to your friend list, you can also watch their profile but not modify it (you can modify it if it's you).
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        initTimer();
        MIN_RANK = 5;
        tab = new Button[MIN_RANK];
        menus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        ids = new String[MIN_RANK];
        images = new ImageView[MIN_RANK];
        myMenus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        if(mAuth.getCurrentUser() != null) {
            tab[0] = findViewById(R.id.top1);
            tab[1] = findViewById(R.id.top2);
            tab[2] = findViewById(R.id.top3);
            tab[3] = findViewById(R.id.top4);
            tab[4] = findViewById(R.id.top5);
            images[0] = findViewById(R.id.profile_picture1);
            images[1] = findViewById(R.id.profile_picture2);
            images[2] = findViewById(R.id.profile_picture3);
            images[3] = findViewById(R.id.profile_picture4);
            images[4] = findViewById(R.id.profile_picture5);
            Button backButton = findViewById(R.id.todayBackButton);
            backButton.setOnClickListener(v -> {
                finish();
            });
            for (int i = 0 ; i < MIN_RANK ; i++) {
                menus[i] = initMenu(i);
                myMenus[i] = initMyMenu();
            }
            initTop(Database.format);
        }
    }

    public void initTop(SimpleDateFormat format) {

        db.mDatabase.child(Database.LEADERBOARD_DAILY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                @SuppressWarnings("unchecked")

                Map<String,HashMap<String, HashMap<String, String>>> leaderBoard = (HashMap<String,HashMap<String, HashMap<String, String>>>) snapshot.getValue();
                TextView myTopText = findViewById(R.id.myTop);
                if(leaderBoard != null && leaderBoard.containsKey(Database.getTodayDate(format))) {
                    Map<String, HashMap<String, String>> leaderBoardOrdered = new TreeMap<>(Database.comparator);
                    Map<String, HashMap<String, String>> idsMap = leaderBoard.get(Database.getTodayDate(format));
                    leaderBoardOrdered.putAll(idsMap);
                    int count = 0;
                    int myTop = 0;
                    for (TreeMap.Entry<String, HashMap<String, String>> entry : leaderBoardOrdered.entrySet()) {
                        String hp = entry.getKey();
                        for(HashMap.Entry<String,String> e : entry.getValue().entrySet()) {
                            int top = count + 1;

                            if (count < MIN_RANK){
                                getImage(e.getKey(), images[count]);
                                ids[count] = e.getKey();
                                String myPts = hp.substring(0, hp.length() - Database.SUFFIX_LEN) + " pts";
                                String str = top + "    " + e.getValue();
                                StringBuilder sb = new StringBuilder();
                                sb.append(str);
                                while(sb.length() <= 40) {
                                    sb.append(' ');
                                }
                                sb.append(myPts);
                                tab[count].setText(sb.toString());

                                if(e.getKey().equals(mAuth.getUid())) {
                                    myTop = top;
                                    tab[count].setOnClickListener(initMyButton(count));
                                }
                                else {
                                    tab[count].setOnClickListener(initButton(count));
                                }
                            }
                            count++;
                        }
                    }
                    while(count < MIN_RANK) {
                        images[count].setImageResource(R.drawable.rounded_logo);
                        tab[count].setText(R.string.NoUser);
                        tab[count].setOnClickListener(null);
                        count++;
                    }

                    if(myTop > 0) {
                            myTopText.setText("your rank : " + myTop);
                    }
                    else {
                        myTopText.setText("your rank : unranked");
                    }
                }
                else {
                    for(int i = 0 ; i < MIN_RANK ; i++) {
                        images[i].setImageResource(R.drawable.rounded_logo);
                        tab[i].setText("");
                        tab[i].setOnClickListener(null);
                    }
                    tab[0].setText(R.string.NoUserInLb);
                    myTopText.setText("your rank : unranked");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebase", "Error:onCancelled", error.toException());
            }
        });

    }

    public PopupMenu.OnMenuItemClickListener initMenu(int index) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.viewProfile:
                    Intent intent = new Intent(this, ViewProfileActivity.class);
                    intent.putExtra(ViewProfileActivity.MESSAGE, ids[index]);
                    startActivity(intent);
                    return true;
                case R.id.addFriendLeaderBoard:
                    db.readField(mAuth.getCurrentUser().getUid(), "friends",task -> {
                        if (!task.isSuccessful()) {
                            Log.e("ERROR", "EREREREROOORORO");
                        }
                        else {
                            Map<String, String> friendList = (Map<String, String>)task.getResult().getValue();
                            if(friendList != null && friendList.containsKey(ids[index])) {
                                Toast.makeText(this, friendList.get(ids[index]) + " is already in your friend list", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                db.addToFriendList(ids[index]);
                                Toast.makeText(this,  "user added to your friend list", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return true;
                default:
                    return false;
            }

        };
    }
    public PopupMenu.OnMenuItemClickListener initMyMenu() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.viewProfileNoFriend:
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        };
    }

    public View.OnClickListener initButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(LeaderBoardActivity.this, v);
            popup.setOnMenuItemClickListener(menus[index]);
            popup.inflate(R.menu.top_menu);
            popup.show();
        };
    }

    public View.OnClickListener initMyButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(LeaderBoardActivity.this, v);
            popup.setOnMenuItemClickListener(myMenus[index]);
            popup.inflate(R.menu.view_profile_menu);
            popup.show();
        };
    }

    public void getImage(String userId, ImageView imageView) {
        db.mDatabase.child(Database.USERS).child(userId).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.getValue() != null){
                        String image = snapshot.getValue().toString();
                        Glide.with(getApplicationContext()).load(image).into(imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebase", "Error:onCancelled", error.toException());
            }
        });
    }

    private void initTimer() {
        TextView tv_countdown = findViewById(R.id.myTimer);

        Calendar start_calendar = Calendar.getInstance();
        Calendar end_calendar = Calendar.getInstance();
        end_calendar.add(Calendar.DAY_OF_YEAR, 1);
        end_calendar.set(Calendar.HOUR_OF_DAY, 0);
        end_calendar.set(Calendar.MINUTE, 0);
        end_calendar.set(Calendar.SECOND, 0);
        long start_millis = start_calendar.getTimeInMillis();
        long end_millis = end_calendar.getTimeInMillis();
        long total_millis = (end_millis - start_millis);

        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                tv_countdown.setText(hours + ":" + minutes + ":" + seconds); //You can compute the millisUntilFinished on hours/minutes/seconds
            }

            @Override
            public void onFinish() {
                tv_countdown.setText(R.string.refresh);
            }
        };
        cdt.start();
    }

}