package ch.epfl.sdp.healthplay.friendlist;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;

public class ListAdapterAddFriend extends ArrayAdapter<Friend> implements Filterable {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final Database database = new Database();
    private final List<Friend> noUpdateItems;

    public ListAdapterAddFriend(Context context, ArrayList<Friend> friendList) {
        super(context, R.layout.fragment_add_friend, friendList);
        noUpdateItems = new ArrayList<>(friendList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Friend friend = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_add_friend_list_item, parent, false);
        }

        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // Lookup view for data population
        TextView friendName = convertView.findViewById(R.id.friendNameAdd);
        Button addFriendButton = convertView.findViewById(R.id.addFriendItem);
        ImageView profileImage = convertView.findViewById(R.id.friendProfilePictureAdd);

        // Remove the selected friend on the button click
        View finalConvertView = convertView;

        // Add the friend to the user friend list
        addFriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String fName = (String) friendName.getText();
                database.addToFriendList(fName);
                Snackbar mySnackbar = Snackbar.make(finalConvertView, "Friend " + fName + " added", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
            }
        });

        // Update the profile picture
        database.readField(friend.getUserName(), "image", new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String image;
                if (task.getResult().getValue() != null) {
                    image = task.getResult().getValue().toString();
                    Glide.with(getContext()).load(image).into(profileImage);
                }
            }
        });

        // Populate the data into the template view using the data object
        friendName.setText(friend.getUserName());
        // Return the completed view to render on screen
        return convertView;
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
            @SuppressWarnings("unchecked")
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
                ArrayList<Friend> FilteredArrayNames = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();
                // Check for each element in the Friend list that the UserId starts with the constraint
                for (int i = 0; i < noUpdateItems.size(); i++) {
                    Friend friends = noUpdateItems.get(i);
                    if (friends.getUserName().toLowerCase().startsWith(constraint.toString()))  {
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
