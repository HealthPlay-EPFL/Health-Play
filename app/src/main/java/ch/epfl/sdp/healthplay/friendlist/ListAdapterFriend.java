package ch.epfl.sdp.healthplay.friendlist;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.Friend;

public class ListAdapterFriend extends ArrayAdapter<Friend> implements Filterable {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final Database database = new Database();
    private final List<Friend> noUpdateItems;

    public ListAdapterFriend(Context context, ArrayList<Friend> friendList) {
        super(context, R.layout.fragment_friend_list_, friendList);
        noUpdateItems = new ArrayList<>(friendList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Friend friend = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_friend_list_item, parent, false);
        }

        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // Lookup view for data population
        TextView friendName = convertView.findViewById(R.id.friendName);
        Button remFriendButton = convertView.findViewById(R.id.removeFriendButton);
        ImageView profileImage = convertView.findViewById(R.id.friendProfilePicture);

        // Remove the selected friend on the button click
        remFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.removeFromFriendList(friendName.getText().toString());
            }
        });

        // Update Profile pictures
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

}
