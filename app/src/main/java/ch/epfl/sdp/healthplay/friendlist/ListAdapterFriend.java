package ch.epfl.sdp.healthplay.friendlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;

public class ListAdapterFriend extends ArrayAdapter<Friend> implements Filterable {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final Database database = new Database();
    private final FriendListItemMode mode;
    private final List<Friend> noUpdateItems;
    private Context context;
    private Button manageFriendButton;


    public ListAdapterFriend(Context context, List<Friend> friendList, FriendListItemMode mode) {
        super(context, R.layout.fragment_friend_list_, friendList);
        this.mode = mode;
        noUpdateItems = new ArrayList<>(friendList);
        this.context = context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Friend friend = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_friend_list_item, parent, false);
        }
        // Block Descendants in order to be able to click on the whole object
        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // Lookup view for data population
        TextView friendName = convertView.findViewById(R.id.friendName);
        manageFriendButton = convertView.findViewById(R.id.manageFriendButton);
        ImageView profileImage = convertView.findViewById(R.id.friendProfilePicture);

        View finalConvertView = convertView;

        // Remove the selected friend on the button click
        if(mode == FriendListItemMode.ADD){
            setFriendButton(R.string.add_friend);
            manageFriendButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    database.addToFriendList(friend.getUserId());
                    Snackbar mySnackbar = Snackbar.make(finalConvertView, "Friend " + friendName.getText() + " added", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            });
        }
        else
        {
            setFriendButton(R.string.remove_friend);
            manageFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.removeFromFriendList(friend.getUserId());
                    Snackbar mySnackbar = Snackbar.make(finalConvertView, "Friend " + friendName.getText() + " removed", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            });
        }

        // Update Profile pictures
        database.readField(friend.getUserId(), "image", new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String image;
                if (task.getResult().getValue() != null) {
                    if(context != null){
                        image = task.getResult().getValue().toString();
                        Glide.with(context).load(image).into(profileImage);
                    }
                }
            }
        });

        // Populate the data into the template view using the data object
        database.readField(friend.getUserId(), Database.USERNAME, new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String username;
                if (task.getResult().getValue() != null) {
                    username = task.getResult().getValue(String.class);
                    friendName.setText(username);
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private void setFriendButton(int text) {
        manageFriendButton.setText(text);
    }

    /**
     * Return the filter
     * @return
     */
    @Override
    public Filter getFilter(){

        return new Filter() {

            /**
             * Publish the results of the filter, i.e apply it
             * @param constraint
             * @param results
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                changeList((List<Friend>) results.values);
                notifyDataSetChanged();
            }

            /**
             * Perform the filtering on the list of all possible Friend
             * @param constraint
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                List<Friend> FilteredArrayNames = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();
                // Check for each element in the Friend list that the UserId starts with the constraint
                for (int i = 0; i < noUpdateItems.size(); i++) {
                    Friend friends = noUpdateItems.get(i);
                    if (friends.getUsername().toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(friends);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };
    }

    /**
     * Update the adapter List
     */
    private void changeList(List<Friend> results) {
        this.clear();
        this.addAll(results);
    }



}
