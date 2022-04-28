package ch.epfl.sdp.healthplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.database.Database;

public class LeaderBoardActivity extends AppCompatActivity{

    private final Database db = new Database();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final static int MAX_RANK = 5;
    private final Button[] tab  = new Button[MAX_RANK];
    private final PopupMenu.OnMenuItemClickListener[] menus = new PopupMenu.OnMenuItemClickListener[5];
    private final String[] ids = new String[5];
    private final PopupMenu.OnMenuItemClickListener[] myMenus = new PopupMenu.OnMenuItemClickListener[5];
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

        if(mAuth.getCurrentUser() != null) {
            db.addHealthPoint(mAuth.getCurrentUser().getUid(), 40);
            tab[0] = findViewById(R.id.top1);
            tab[1] = findViewById(R.id.top2);
            tab[2] = findViewById(R.id.top3);
            tab[3] = findViewById(R.id.top4);
            tab[4] = findViewById(R.id.top5);

            for (int i = 0 ; i < MAX_RANK ; i++) {
                menus[i] = initMenu(i);
                myMenus[i] = initMyMenu(i);
            }

            initTop5();
        }
    }

    private void initTop5() {

        db.mDatabase.child(Database.LEADERBOARD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                @SuppressWarnings("unchecked")
                HashMap<String,HashMap<String, ArrayList<String>>> leaderBoard = (HashMap<String,HashMap<String, ArrayList<String>>>) snapshot.getValue();
                TextView myTopText = findViewById(R.id.myTop);
                if(leaderBoard != null && leaderBoard.containsKey(Database.getTodayDate())) {
                    TreeMap<String, ArrayList<String>> leaderBoardOrdered = new TreeMap<>(Database.comparator);
                    leaderBoardOrdered.putAll(leaderBoard.get(Database.getTodayDate()));
                    int count = 0;
                    int myTop = 0;
                    for (TreeMap.Entry<String, ArrayList<String>> entry : leaderBoardOrdered.entrySet()) {
                        String hp = entry.getKey();
                        for(String e: entry.getValue()) {
                            int top = count + 1;

                            if (count < MAX_RANK){
                                ids[count] = e;
                                tab[count].setText(top + " | " + e + " | " + hp);
                                if(e.equals(mAuth.getUid())) {
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
                    while(count < MAX_RANK) {
                            tab[count].setText(R.string.NoUser);
                            tab[count].setOnClickListener(null);
                            count++;
                    }

                    if(myTop > 0) {
                        if(myTop > MAX_RANK) {
                            myTopText.setText("(you are currently rank " + myTop + ")");
                        }
                        else {
                            myTopText.setText(R.string.CongratsTop5);
                        }
                    }
                    else {
                        myTopText.setText(R.string.UnrankedTop5);
                    }
                }
                else {
                    for(int i = 0 ; i < MAX_RANK ; i++) {
                        tab[i].setText("");
                        tab[i].setOnClickListener(null);
                    }
                    tab[0].setText(R.string.NoUserInLb);
                    myTopText.setText(R.string.UnrankedTop5);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebase", "Error:onCancelled", error.toException());
            }
        });

    }

    private PopupMenu.OnMenuItemClickListener initMenu(int index) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.viewProfile:
                    Intent intent = new Intent(this, ViewProfileActivity.class);
                    intent.putExtra(ViewProfileActivity.MESSAGE, ids[index]);
                    startActivity(intent);
                    return true;
                case R.id.addFriendLeaderBoard:
                    db.addToFriendList(ids[index]);
                    return true;
                default:
                    return false;
            }

        };
    }
    private PopupMenu.OnMenuItemClickListener initMyMenu(int index) {
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

    private View.OnClickListener initButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(LeaderBoardActivity.this, v);
            popup.setOnMenuItemClickListener(menus[index]);
            popup.inflate(R.menu.top_menu);
            popup.show();
        };
    }

    private View.OnClickListener initMyButton(int index) {
        return v -> {
            PopupMenu popup = new PopupMenu(LeaderBoardActivity.this, v);
            popup.setOnMenuItemClickListener(myMenus[index]);
            popup.inflate(R.menu.view_profile_menu);
            popup.show();
        };
    }

}