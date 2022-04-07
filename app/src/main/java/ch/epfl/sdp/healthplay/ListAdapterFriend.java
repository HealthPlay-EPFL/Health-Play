package ch.epfl.sdp.healthplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;
import ch.epfl.sdp.healthplay.database.Plant;

public class ListAdapterFriend extends ArrayAdapter<Friend> {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final Database database = new Database();

    public ListAdapterFriend(Context context, ArrayList<Friend> friendList) {
        super(context, R.layout.fragment_friend_list_, friendList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Friend friend = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_friend_list_item, parent, false);
        }
        // Lookup view for data population
        TextView friendName = (TextView) convertView.findViewById(R.id.friendName);
        Button remFriendButton = convertView.findViewById(R.id.removeFriendButton);

        remFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromFriendList(friendName.getText().toString());
            }
        });


        // Populate the data into the template view using the data object
        friendName.setText(friend.getUserName());
        // Return the completed view to render on screen
        return convertView;
    }

    private void removeFromFriendList(String friendUserId) {
        System.out.println(friendUserId);
        if(auth.getCurrentUser() != null) {
            database.mDatabase.child(Database.USERS)
                    .child(auth.getUid())
                    .child("friends")
                    .child(friendUserId)
                    .setValue(false);
        }
    }
}
