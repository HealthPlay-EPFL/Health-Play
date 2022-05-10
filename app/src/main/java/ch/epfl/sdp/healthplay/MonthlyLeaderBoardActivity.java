package ch.epfl.sdp.healthplay;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import ch.epfl.sdp.healthplay.database.Database;

public class MonthlyLeaderBoardActivity extends LeaderBoardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_leader_board);
        initTimer();
        MIN_RANK = 5;
        tab = new Button[MIN_RANK];
        menus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        ids = new String[MIN_RANK];
        images = new ImageView[MIN_RANK];
        myMenus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        if(mAuth.getCurrentUser() != null) {
            db.addHealthPoint(mAuth.getCurrentUser().getUid(), 40);
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
            Button todayButton = findViewById(R.id.todayButton);
            todayButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(),LeaderBoardActivity.class);
                startActivity(intent);
            });
            Button backButton = findViewById(R.id.monthBackButton);
            backButton.setOnClickListener(v -> {
                finish();
            });
            for (int i = 0 ; i < MIN_RANK ; i++) {
                menus[i] = initMenu(i);
                myMenus[i] = initMyMenu();
            }
            initTop(Database.formatYearMonth);
        }
    }


    public void initTop(SimpleDateFormat format) {

        db.mDatabase.child(Database.LEADERBOARD_MONTHLY).addValueEventListener(new ValueEventListener() {
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

    public View.OnClickListener initButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(MonthlyLeaderBoardActivity.this, v);
            popup.setOnMenuItemClickListener(menus[index]);
            popup.inflate(R.menu.top_menu);
            popup.show();
        };
    }

    public View.OnClickListener initMyButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(MonthlyLeaderBoardActivity.this, v);
            popup.setOnMenuItemClickListener(myMenus[index]);
            popup.inflate(R.menu.view_profile_menu);
            popup.show();
        };
    }

    private void initTimer() {
        TextView tv_countdown = findViewById(R.id.myTimer);

        Calendar start_calendar = Calendar.getInstance();
        Calendar end_calendar = Calendar.getInstance();
        end_calendar.add(Calendar.MONTH, 1);
        end_calendar.set(Calendar.DAY_OF_MONTH, 0);
        end_calendar.set(Calendar.HOUR_OF_DAY, 0);
        end_calendar.set(Calendar.MINUTE, 0);
        end_calendar.set(Calendar.SECOND, 0);
        long start_millis = start_calendar.getTimeInMillis();
        long end_millis = end_calendar.getTimeInMillis();
        long total_millis = (end_millis - start_millis);

        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                tv_countdown.setText(days + ":" + hours + ":" + minutes + ":" + seconds); //You can compute the millisUntilFinished on hours/minutes/seconds
            }

            @Override
            public void onFinish() {
                tv_countdown.setText(R.string.refresh);
            }
        };
        cdt.start();
    }


}