package ch.epfl.sdp.healthplay;

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

import ch.epfl.sdp.healthplay.auth.ProfileFragment;
import ch.epfl.sdp.healthplay.database.Database;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderBoardFragment extends Fragment {

    public final Database db = new Database();
    public final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public int MIN_RANK;
    public Button[] tab;
    public PopupMenu.OnMenuItemClickListener[] menus;
    public String[] ids;
    public PopupMenu.OnMenuItemClickListener[] myMenus;
    public TextView[] topTexts;
    public ImageView[] images;
    public final static int STRING_MAX_LEN = 25;
    public final static String RANK_NAME_SEPARATOR = "    ";
    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderBoardFragment newInstance(String param1, String param2) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        initTimer();
        MIN_RANK = 5;
        tab = new Button[MIN_RANK];
        menus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        ids = new String[MIN_RANK];
        images = new ImageView[MIN_RANK];
        myMenus = new PopupMenu.OnMenuItemClickListener[MIN_RANK];
        topTexts = new TextView[MIN_RANK];
        if(mAuth.getCurrentUser() != null) {
            //db.addHealthPoint(mAuth.getCurrentUser().getUid(), 40);
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
            Button backButton = view.findViewById(R.id.todayBackButton);
            backButton.setOnClickListener(v -> {
               exitLeaderBoard();
            });
            for (int i = 0 ; i < MIN_RANK ; i++) {
                menus[i] = initMenu(i);
                myMenus[i] = initMyMenu();
            }
            initTop(Database.format);
        }

        return view;
    }

    /**
     * initialize the leaderboard and add a listener to its values on the database
     * @param format the format used to store the date in the daily leaderboard
     */
    public void initTop(SimpleDateFormat format) {

        db.mDatabase.child(Database.LEADERBOARD_DAILY).addValueEventListener(new ValueEventListener() {
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
                        myTopText.setText("your rank : unranked");
                    }
                }
                else {
                    for(int i = 0 ; i < MIN_RANK ; i++) {
                        images[i].setImageResource(R.drawable.rounded_logo);
                        tab[i].setText("");
                        tab[i].setOnClickListener(null);
                    }
                    myTopText.setText("your rank : unranked");

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
     * @return an onMenu listener that add the index player as a friend if you clicked "add friend" and that send you to the profile of the idx player if you clicked on "view profile"
     */
    public PopupMenu.OnMenuItemClickListener initMenu(int index) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.viewProfile:
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setReorderingAllowed(true);
                    fragmentTransaction.replace(R.id.fragmentContainerView, viewProfileFragment.newInstance(ids[index], ""));
                    fragmentTransaction.commit();
                    return true;
                case R.id.addFriendLeaderBoard:
                    db.readField(mAuth.getCurrentUser().getUid(), Database.FRIEND,task -> {
                        if (!task.isSuccessful()) {
                            Log.e("ERROR", "EREREREROOORORO");
                        }
                        else {
                            Map<String, String> friendList = (Map<String, String>)task.getResult().getValue();
                            if(friendList != null && friendList.containsKey(ids[index])) {
                                Toast.makeText(getActivity(), friendList.get(ids[index]) + " is already in your friend list", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                db.addToFriendList(ids[index]);
                                Toast.makeText(getActivity(),  "user added to your friend list", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return true;
                default:
                    return false;
            }

        };
    }

    /**
     *
     * @return an onMenu listener send you to your profile if you clicked "view my profile"
     */
    public PopupMenu.OnMenuItemClickListener initMyMenu() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.viewProfileNoFriend:
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setReorderingAllowed(true);
                    fragmentTransaction.replace(R.id.fragmentContainerView, new ProfileFragment());
                    fragmentTransaction.commit();
                    return true;
                default:
                    return false;
            }
        };
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

    public void getImage(String userId, ImageView imageView) {
        db.mDatabase.child(Database.USERS).child(userId).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.getValue() != null){
                        String image = snapshot.getValue().toString();
                        if(getActivity() != null) {
                            Glide.with(getActivity()).load(image).into(imageView);
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initTimer() {
        TextView tv_countdown = view.findViewById(R.id.myTimer);

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

                tv_countdown.setText(hours + ":" + minutes + ":" + seconds);
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
        fragmentTransaction.replace(R.id.fragmentContainerView, new MonthlyLeaderBoardFragment());
        fragmentTransaction.commit();
    }

}