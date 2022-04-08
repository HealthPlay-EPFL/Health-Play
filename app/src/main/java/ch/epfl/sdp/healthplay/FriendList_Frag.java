package ch.epfl.sdp.healthplay;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendList_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendList_Frag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final Database database = new Database();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private Map<String, Boolean> friends;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendList_Frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendList_Frag.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendList_Frag newInstance(String param1, String param2) {
        FriendList_Frag fragment = new FriendList_Frag();
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
        View view = inflater.inflate(R.layout.fragment_friend_list_, container, false);

        Button calendarButton = view.findViewById(R.id.friendToCalendar);
        Button addFriendButton = view.findViewById(R.id.addFriendBouton);
        ListView listView = view.findViewById(R.id.friendList);

        // Switch to the Home Fragment
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ;
                switchToFragment(new Frag_Home());
            }
        }
        );

        // Switch to the AddFriend Fragment
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(new AddFriendFragment());
            }
        }
        );

        // Get the Friend List of the current User
        if(auth.getCurrentUser() != null) {
            database.readField(auth.getCurrentUser().getUid(), "friends", new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    friends = (Map<String, Boolean>) task.getResult().getValue();
                    List<String> toRemove = new ArrayList<>();
                    List<Friend> friendList = new ArrayList<Friend>();
                    for (String friend : friends.keySet()
                    ) {
                        if (!friends.get(friend)) {
                            // We need to create another list, because you cannot forEach in a changing List
                            toRemove.add(friend);
                        } else {
                            friendList.add(new Friend(friend));
                        }
                    }
                    for (String rem : toRemove
                    ) {
                        friends.remove(rem);
                    }

                    buildListView(view, listView, friendList);
                }
            });

            // Listen to changes to the FriendList of the User
            database.mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Get the changes
                    Map<String, Boolean> value = (Map<String, Boolean>) snapshot.getValue();
                    List<String> toRemove = new ArrayList<>();
                    List<Friend> friendList = new ArrayList<Friend>();
                    for (String friend : value.keySet()
                    ) {
                        if (!value.get(friend)) {
                            // We need to create another list, because you cannot forEach in a changing List
                            toRemove.add(friend);
                        } else {
                            friendList.add(new Friend(friend));
                        }
                    }
                    for (String rem : toRemove
                    ) {
                        value.remove(rem);
                    }

                    updateListView(view, listView, friendList);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println(error.toString());
                }
            }

            );
        }


        return view;
    }

    /**
     * Build the List view
     * @param view
     * @param listView
     * @param friendList
     */
    private void buildListView(View view, ListView listView, List<Friend> friendList) {
        ArrayList<Friend> arrayOfUsers = new ArrayList<Friend>();
        // Create the adapter to convert the array to views
        ListAdapterFriend adapter = new ListAdapterFriend(view.getContext(), arrayOfUsers);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
        adapter.addAll(friendList);
    }

    /**
     * Update the ListView adapter with the given List of Friend in parameter
     * @param view
     * @param listView
     * @param friendList
     */
    private void updateListView(View view, ListView listView, List<Friend> friendList){
        ListAdapterFriend adapter = (ListAdapterFriend)listView.getAdapter();
        if(adapter != null){
            adapter.clear();
            adapter.addAll(friendList);
        }

    }

    /**
     * Switch to the given fragment
     * @param fragment
     */
    private void switchToFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.commit();
    }


}