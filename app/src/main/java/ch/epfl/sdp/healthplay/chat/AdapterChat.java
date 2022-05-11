package ch.epfl.sdp.healthplay.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import de.hdodenhof.circleimageview.CircleImageView;

//This class is the Adapter for all the messages in a chat
public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<ModelChat> list;
    private String imageUrl;
    private FirebaseUser firebaseUser;
    private Database database;

    /**
     * Construct an AdapterChat
     *
     * @param context
     * @param list
     * @param imageUrl
     */
    public AdapterChat(Context context, List<ModelChat> list, String imageUrl) {
        //Initialize the context, the list of messages and the imageUrl of the friend
        this.context = context;
        this.list = list;
        this.imageUrl = imageUrl;
        database = new Database();
    }

    /**
     * Create a holder depending on the sender of the message
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //Left message = received message
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
        }
        //Right message = sent message
        else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
        }
        return new MyHolder(view);
    }

    /**
     * Show the messages in the right layout and initialise it
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        //Get the message, timeStamp and type of the message at the position position
        String message = list.get(position).getMessage();
        String timeStamp = list.get(position).getTimestamp();
        String type = list.get(position).getType();

        //Get the timeStamp of the message
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String timeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //Set the message and the timeStamp of the shown message
        holder.message.setText(message);
        holder.time.setText(timeDate);
        if (imageUrl != null) {
            Glide.with(context).load(imageUrl).into(holder.image);
        } else {
            Glide.with(context).load(R.drawable.profile_icon).into(holder.image);
        }
        //if text, show a text and hide the image container
        if (type.equals("text")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.mimage.setVisibility(View.GONE);
            holder.message.setText(message);
        }
        //if image, show a image and hide the text container
        else {
            holder.message.setVisibility(View.GONE);
            holder.mimage.setVisibility(View.VISIBLE);
            Glide.with(context).load(message).into(holder.mimage);
        }
        //Handler click on messages
        holder.msglayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AlertDialog with a warning and a button to delete the message
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.delete_message_en));
                builder.setMessage(context.getString(R.string.are_you_sure_to_delete_this_message_en));
                builder.setPositiveButton(context.getString(R.string.delete_en), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMsg(position);
                    }
                });
                //Handle the click on the Cancel button
                builder.setNegativeButton(context.getString(R.string.cancel_en), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the AlertDialog
                        dialog.dismiss();
                    }
                });
                //Show the AlertDialog
                builder.create().show();
            }
        });
    }

    /**
     * Delete the message at the given position
     *
     * @param position
     */
    private void deleteMsg(int position) {
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Get the timestamp of the message
        String msgTimeStamp = list.get(position).getTimestamp();
        DatabaseReference dbRef = database.mDatabase.child("Chats");

        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        //Remove the message in the Database and print a Toast
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //If the user tries to delete one of his message
                    if (dataSnapshot1.child("sender").getValue().equals(myUid)) {
                        dataSnapshot1.getRef().removeValue();
                        Toast.makeText(context, context.getString(R.string.message_deleted_en), Toast.LENGTH_LONG).show();
                    }
                    //If the user tries to delete the message of another user
                    else {
                        Toast.makeText(context, context.getString(R.string.delete_your_own_messages_en), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get the number of element in the Adapter list
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * Get the type of the message at the given position
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    // View holder to handle the layout's view
    class MyHolder extends RecyclerView.ViewHolder {

        private CircleImageView image;
        private ImageView mimage;
        private TextView message, time, isSee;
        private LinearLayout msglayout;

        /**
         * Constructor
         * @param itemView
         */
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.msgProfile);
            message = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.msgTime);
            isSee = itemView.findViewById(R.id.isSeen);
            msglayout = itemView.findViewById(R.id.msgLayout);
            mimage = itemView.findViewById(R.id.images);
        }
    }
}
