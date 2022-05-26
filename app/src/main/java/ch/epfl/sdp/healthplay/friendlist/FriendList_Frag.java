package ch.epfl.sdp.healthplay.friendlist;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.sdp.healthplay.Frag_Home;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;
import ch.epfl.sdp.healthplay.navigation.FragmentNavigation;
import ch.epfl.sdp.healthplay.viewProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class  FriendList_Frag extends Fragment {

    private final Database database = new Database();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    public FriendList_Frag() {
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
        View view = inflater.inflate(R.layout.fragment_friend_list_, container, false);

        Button calendarButton = view.findViewById(R.id.friendToCalendar);
        Button addFriendButton = view.findViewById(R.id.addFriendBouton);
        ListView listView = view.findViewById(R.id.friendList);
        FragmentManager fragmentManager = getParentFragmentManager();

        // Switch to the Home Fragment
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ;
                FragmentNavigation.switchToFragment(fragmentManager, new Frag_Home());
            }
        }
        );

        // Switch to the AddFriend Fragment
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(auth.getCurrentUser() != null ){
                    FragmentNavigation.switchToFragment(fragmentManager, new AddFriendFragment());
                }
                else{
                    Snackbar mySnackbar = Snackbar.make(view, "Cannot add friends when offline", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        }
        );

        // Get the Friend List of the current User
        if(auth.getCurrentUser() != null) {
            Map<String, String> friends = database.getFriendList();

            buildListView(view, listView, buildFriendListFromFirebase(friends));

            // Listen to changes to the FriendList of the User
            database.mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Get the changes
                    Map<String, String> value = (Map<String, String>) snapshot.getValue();
                    if(value != null) {

                        updateListView(listView, buildFriendListFromFirebase(value));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            }

            );
        }
        //Go to the profile of the friend
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend selectedFriend = (Friend)listView.getAdapter().getItem(position);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.replace(R.id.fragmentContainerView, viewProfileFragment.newInstance(selectedFriend.getUserId()));
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    /**
     * Build the friend list from the map returned by Firebase
     * @param map
     * @return
     */
    private List<Friend> buildFriendListFromFirebase(Map<String, String> map){
        List<Friend> friendList = new ArrayList<>();;
        if(map != null) {
            for (String friend : map.keySet()
            ) {
                friendList.add(new Friend(friend, map.get(friend)));
            }
        }
        return friendList;
    }
    /**
     * Build the List view
     * @param view
     * @param listView
     * @param friendList
     */
    private void buildListView(View view, ListView listView, List<Friend> friendList) {
        List<Friend> arrayOfUsers = new ArrayList<Friend>();
        // Create the adapter to convert the array to views
        ListAdapterFriend adapter = new ListAdapterFriend(view.getContext(), arrayOfUsers, false);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
        adapter.addAll(friendList);
    }

    /**
     * Update the ListView adapter with the given List of Friend in parameter
     * @param listView
     * @param friendList
     */
    private void updateListView(ListView listView, List<Friend> friendList){
        //Get the Adapter (i.e the list of item)
        ListAdapterFriend adapter = (ListAdapterFriend)listView.getAdapter();
        if(adapter != null){
            adapter.clear();
            adapter.addAll(friendList);
        }

    }
}