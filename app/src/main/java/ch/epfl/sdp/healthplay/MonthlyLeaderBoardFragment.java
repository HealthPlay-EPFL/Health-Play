package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyLeaderBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyLeaderBoardFragment extends LeaderBoardFragment {

    private View view;


    public MonthlyLeaderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_monthly_leader_board, container, false);

        initTimer();
        MIN_RANK = 5;
        tab = new Button[MIN_RANK];
        menus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        ids = new String[MIN_RANK];
        images = new ImageView[MIN_RANK];
        myMenus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        topTexts = new TextView[MIN_RANK];
        if(mAuth.getCurrentUser() != null) {
            db.addHealthPoint(mAuth.getCurrentUser().getUid(), 40);
            tab[0] = view.findViewById(R.id.top1);
            tab[1] = view.findViewById(R.id.top2);
            tab[2] = view.findViewById(R.id.top3);
            tab[3] = view.findViewById(R.id.top4);
            tab[4] = view.findViewById(R.id.top5);
            topTexts[0] = view.findViewById(R.id.topText1);
            topTexts[1] = view.findViewById(R.id.topText2);
            topTexts[2] = view.findViewById(R.id.topText3);
            topTexts[3] = view.findViewById(R.id.topText4);
            topTexts[4] = view.findViewById(R.id.topText5);

            images[0] = view.findViewById(R.id.profile_picture1);
            images[1] = view.findViewById(R.id.profile_picture2);
            images[2] = view.findViewById(R.id.profile_picture3);
            images[3] = view.findViewById(R.id.profile_picture4);
            images[4] = view.findViewById(R.id.profile_picture5);
            Button todayButton = view.findViewById(R.id.todayButton);
            todayButton.setOnClickListener(v -> {
                todayLeaderBoard();
            });
            Button backButton = view.findViewById(R.id.monthBackButton);
            backButton.setOnClickListener(v -> {
                exitLeaderBoard();
            });
            for (int i = 0 ; i < MIN_RANK ; i++) {
                menus[i] = initMenu(i);
                myMenus[i] = initMyMenu();
            }
            initTop(Database.formatYearMonth);
        }

        return view;
    }

    /**
     * initialize the leaderboard and add a listener to its values on the database
     * @param format the format used to store the date in the monthly leaderboard
     */
    public void initTop(SimpleDateFormat format) {

        db.mDatabase.child(Database.LEADERBOARD_MONTHLY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                @SuppressWarnings("unchecked")

                Map<String,HashMap<String, HashMap<String, String>>> leaderBoard = (HashMap<String,HashMap<String, HashMap<String, String>>>) snapshot.getValue();
                TextView myTopText = view.findViewById(R.id.myTop);
                if(leaderBoard != null && leaderBoard.containsKey(Database.getTodayDate(format))) {
                    Map<String, HashMap<String, String>> leaderBoardOrdered = new TreeMap<>(Database.comparator);
                    Map<String, HashMap<String, String>> idsMap = leaderBoard.get(Database.getTodayDate(format));
                    leaderBoardOrdered.putAll(idsMap);
                    int count = 0;
                    int myTop = -1;
                    for (TreeMap.Entry<String, HashMap<String, String>> entry : leaderBoardOrdered.entrySet()) {
                        String hp = entry.getKey();
                        for(HashMap.Entry<String,String> e : entry.getValue().entrySet()) {
                            int top = count + 1;
                            String myPts = hp.substring(0, hp.length() - Database.SUFFIX_LEN);

                            if (count < MIN_RANK && Long.parseLong(myPts) > 0){
                                getImage(e.getKey(), images[count]);
                                ids[count] = e.getKey();
                                String str = top + RANK_NAME_SEPARATOR + e.getValue() + "\n" + "  " + RANK_NAME_SEPARATOR + myPts + " pts";
                                topTexts[count].setText(str);
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
                        topTexts[count].setText("");
                        tab[count].setOnClickListener(null);
                        count++;
                    }

                    if(myTop > 0) {
                        myTopText.setText("your rank : " + myTop);
                    }
                    else {
                        myTopText.setText(R.string.unranked);
                    }
                }
                else {
                    for(int i = 0 ; i < MIN_RANK ; i++) {
                        images[i].setImageResource(R.drawable.rounded_logo);
                        topTexts[i].setText("");
                        tab[i].setOnClickListener(null);
                    }
                    myTopText.setText(R.string.unranked);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebase", "Error:onCancelled", error.toException());
            }
        });

    }

    /**
     *
     * @param index the index of the player you clicked on
     * @return an on click listener that show a menu where you can either add friends or view profiles when you click on the idx th player (the menu only appears if you clicked on a player that is not yourself)
     */
    public View.OnClickListener initButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(getActivity(), v);
            popup.setOnMenuItemClickListener(menus[index]);
            popup.inflate(R.menu.top_menu);
            popup.show();
        };
    }

    /**
     *
     * @param index the index of the player you clicked on
     * @return an on click listener that show a menu where you can either add friends or view profiles when you click on the idx th player.(the menu only appears if you clicked on a player that is yourself)
     */
    public View.OnClickListener initMyButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(getActivity(), v);
            popup.setOnMenuItemClickListener(myMenus[index]);
            popup.inflate(R.menu.view_profile_menu);
            popup.show();
        };
    }

    private void initTimer() {
        TextView tv_countdown = view.findViewById(R.id.myTimer);

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

                tv_countdown.setText(days + ":" + hours + ":" + minutes + ":" + seconds);
            }

            @Override
            public void onFinish() {
                tv_countdown.setText(R.string.refresh);
            }
        };
        cdt.start();
    }

    private void exitLeaderBoard() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragmentContainerView, new GameMenuFragment());
        fragmentTransaction.commit();
    }

    private void todayLeaderBoard() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragmentContainerView, new LeaderBoardFragment());
        fragmentTransaction.commit();
    }
}