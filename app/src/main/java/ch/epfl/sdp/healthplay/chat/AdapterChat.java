package ch.epfl.sdp.healthplay.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Map;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntCreateJoinLobbyActivity;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntJoinLobbyActivity;
import ch.epfl.sdp.healthplay.planthunt.PlanthuntWaitLobbyActivity;
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
    private String username = "";

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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database.readField(firebaseUser.getUid(), Database.USERNAME, new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                username = task.getResult().getValue(String.class);
            }
        });
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
        //if text or invitation, show a text and hide the image container
        if (type.equals("text") || type.equals("invitation")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.mImage.setVisibility(View.GONE);
            holder.message.setText(message);
        }
        //if image, show a image and hide the text container
        else{
            holder.message.setVisibility(View.GONE);
            holder.mImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(message).into(holder.mImage);
        }
        //Handler click on messages
        holder.msgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AlertDialog with a warning and a button to delete the message
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //If the message is an invitation
                if(type.equals("invitation")){
                    Intent intent = new Intent(context, PlanthuntWaitLobbyActivity.class);
                    String lobbyName = message.split(":")[1].trim();

                    intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
                    intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, username);
                    intent.putExtra(PlanthuntCreateJoinLobbyActivity.HOST_TYPE, PlanthuntCreateJoinLobbyActivity.PLAYER);

                    builder.setTitle(context.getString(R.string.invitation_en));
                    builder.setMessage(context.getString(R.string.do_you_want_to_join_the_lobby_en) + " " + lobbyName + " ?");
                    //Handle the click on the Join button
                    builder.setPositiveButton(context.getString(R.string.join_en), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.getLobbyPlayerCount(lobbyName, Database.NBR_PLAYERS, task2 -> {
                                if (!task2.isSuccessful()) {
                                    Log.e("ERROR", "Lobby does not exist!");
                                }
                                database.getLobbyPlayerCount(lobbyName, Database.MAX_NBR_PLAYERS, task3 -> {
                                    if (!task3.isSuccessful()) {
                                        Log.e("ERROR", "Lobby does not exist!");
                                    }
                                    if (Math.toIntExact((long) task2.getResult().getValue()) < Math.toIntExact((long) task3.getResult().getValue())) {
                                        database.addUserToLobby(lobbyName, username);
                                        context.startActivity(intent);
                                    } else {
                                        Snackbar.make(holder.itemView,"The lobby is full", Snackbar.LENGTH_SHORT ).show();
                                    }
                                });
                            });


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
                    //Handle the click on the Delete button
                    builder.setNegativeButton(context.getString(R.string.delete_en), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //close the AlertDialog
                            deleteMsg(position);
                        }
                    });

                }
                //If the message is a text or an image
                else{
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
                }

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
        private ImageView mImage;
        private TextView message, time, isSee;
        private LinearLayout msgLayout;

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
            msgLayout = itemView.findViewById(R.id.msgLayout);
            mImage = itemView.findViewById(R.id.images);
        }
    }
}
