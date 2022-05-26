package ch.epfl.sdp.healthplay.friendlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.ViewProfileActivity;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;
import ch.epfl.sdp.healthplay.navigation.FragmentNavigation;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final Database database = new Database();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    public AddFriendFragment() {
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
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        Button backButton = view.findViewById(R.id.backButton);
        EditText editText = view.findViewById(R.id.friendSearch);
        ListView listView = view.findViewById(R.id.allUserList);

        List<String> allUsers = new ArrayList<>();
        if(auth.getCurrentUser() != null) {
            // Get the list of all users in the Database
            database.mDatabase.child(Database.USERS).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.getResult().exists()){
                        Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) task.getResult().getValue();
                        //allUsers.addAll(((Map<String, Object>) task.getResult().getValue()).keySet());
                        allUsers.addAll(map.keySet());
                        List<Friend> allPossibleFriend = new ArrayList<>();

                        for (String user : allUsers) {
                            allPossibleFriend.add(new Friend(user, Objects.requireNonNull(map.get(user)).get(Database.USERNAME)));
                        }

                        buildListView(view, listView, allPossibleFriend);
                    }
                }
            });
        }

        // Go back to the FriendList
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToFriendList();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((ListAdapterFriend)listView.getAdapter()).getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend selectedFriend = (Friend)listView.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                intent.putExtra(ViewProfileActivity.MESSAGE, selectedFriend.getUserId());
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Switch the current fragment (AddFriendFragment) to the FriendListFragment
     */
    private void backToFriendList(){
        FragmentNavigation.switchToFragment(getParentFragmentManager(), new FriendList_Frag());
    }

    /**
     * Build the ListView with the list of Friend
     * @param view
     * @param listView
     * @param friendList
     */
    public void buildListView(View view, ListView listView, List<Friend> friendList) {
        List<Friend> arrayOfUsers = new ArrayList<>(friendList);

        // Sort Friend by their name
        Collections.sort(arrayOfUsers, new Comparator<Friend>() {
            @Override
            public int compare(Friend f1, Friend f2) {
                return f1.getUsername().compareTo(f2.getUsername());
            }
        });

        // Create the adapter to convert the array to views
        ListAdapterFriend adapter = new ListAdapterFriend(view.getContext(), arrayOfUsers, true);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);

    }

}