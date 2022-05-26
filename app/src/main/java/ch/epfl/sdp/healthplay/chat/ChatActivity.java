package ch.epfl.sdp.healthplay.chat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

//Activity of the chat
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView profile;
    private TextView name, userStatus;
    private EditText msg;
    private ImageButton send, attach;
    private FirebaseAuth firebaseAuth;
    private String uid, myUid, image;
    private List<ModelChat> chatList;
    private AdapterChat adapterChat;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int STORAGE_REQUEST = 200;
    private String[] storagePermission;
    private Uri imageUri = null;
    private Database firebaseDatabase;
    private DatabaseReference selfRef;


    /**
     * View creation
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();

        // initialise the text views and layouts
        profile = findViewById(R.id.profiletv);
        name = findViewById(R.id.nameptv);
        userStatus = findViewById(R.id.onlinetv);
        msg = findViewById(R.id.messaget);
        send = findViewById(R.id.sendmsg);
        attach = findViewById(R.id.attachbtn);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        uid = getIntent().getStringExtra("uid");

        // initialize database
        firebaseDatabase = new Database();
        //Get the current user id
        getCurrentUser();
        selfRef = firebaseDatabase.mDatabase.child(Database.USERS).child(myUid);


        // initialising permissions
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //Handle the click on the attach button
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Choose if you want to to send an image of something else
                //Only image are implemented but we can add files and camera easily
                showImagePicDialog();
            }
        });

        //Handle the click on the send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString().trim();
                // if the message is empty, show the toast
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChatActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
                }
                // if the message isn't empty, send the message
                else {
                    sendMessage(message);
                }
                // reset the message field
                msg.setText("");
                // The user is not typing anymore
                setTypingStatus("notTyping");
            }
        });

        //Listen to changes in the message text field
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the user starts writing a message, change his typing status to the user id of the friend his writing to
                setTypingStatus(uid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });

        //Listen to changes in the database of the friend you're chatting to, and update his information printed on screen
        firebaseDatabase.mDatabase.child(Database.USERS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // retrieve user data
                if(dataSnapshot.exists()) {

                    String username = (String) dataSnapshot.child(Database.USERNAME).getValue();
                    image = (String) dataSnapshot.child("image").getValue();
                    String onlineStatus = (String) dataSnapshot.child("onlineStatus").getValue();
                    String typingTo = (String) dataSnapshot.child("typingTo").getValue();

                    //If his typingStatus is equal to the user id, then show that he is typing
                    if (typingTo.equals(myUid)) {// if user is typing to my chat
                        userStatus.setText(R.string.typing_en);// type status as typing
                    }
                    //If not typing
                    else {
                        //If online
                        if (onlineStatus.equals("online") || onlineStatus.equals("offline")) {
                            userStatus.setText(onlineStatus);
                        }
                        //If not online, show the last time he was seen
                        else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                            String timeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                            userStatus.setText(getString(R.string.last_seen_en) + timeDate);
                        }
                    }
                    //Set the name of the friend
                    name.setText(username);
                    try {
                        Glide.with(ChatActivity.this).load(image).placeholder(R.drawable.profile_icon).into(profile);
                    } catch (Exception e) {

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        //Show the messages
        readMessages();
    }

    /**
     * When we start the Activity
     */
    @Override
    protected void onStart() {
        firebaseDatabase.setOnlineStatus("online");
        super.onStart();
    }

    /**
     * Set the typingStatus
     * @param typing
     */
    private void setTypingStatus(String typing) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        selfRef.updateChildren(hashMap);
    }

    /**
     * Get all the messages between the selected friend and yourself, and display them
     */
    private void readMessages() {
        // show message after retrieving data
        chatList = new ArrayList<>();
        DatabaseReference dbRef = firebaseDatabase.mDatabase.child(Database.CHATS);

        //Listen to the database where the messages are stored
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //Get the message
                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                    //Check if the message is part of the conversation with your selected friend
                    if (modelChat.getSender().equals(myUid) &&
                            modelChat.getReceiver().equals(uid) ||
                            modelChat.getReceiver().equals(myUid)
                                    && modelChat.getSender().equals(uid)) {
                        //Add the chat in chatList
                        chatList.add(modelChat);
                    }
                    //Create the Adapter with all the messages
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, image);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Show the options of special messages (i.e images)
     */
    private void showImagePicDialog() {
        //We can add options in the future
        String[] options = {"Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle(getString(R.string.pick_image_from_en));
        //Handle click
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if permission is not given
                if (!checkStoragePermission()) {
                    // request for permission
                    requestStoragePermission();
                }
                else {
                    // if already access granted then pick
                    pickFromGallery();
                }
            }
        });
        //build the AlertDialog
        builder.create().show();
    }

    /**
     * Handle the RequestPermissionResult
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_REQUEST) {
            if (grantResults.length > 0) {
                //Buggy so I don't really check it
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                pickFromGallery(); // if access granted then pick
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Handle when the user selected an image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageUri = data.getData(); // get image data to upload
                //Try to send the image as a message
                try {
                    sendImageMessage(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Send the image as a message
     * @param imageUri
     * @throws IOException
     */
    private void sendImageMessage(Uri imageUri) throws IOException {
        //ProgressDialog while the image is sent
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image");
        dialog.show();

        // If we are sending image as a message then we need to find the url of
        // image after uploading it to firebase storage
        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "ChatImages/" + "post" + timestamp; // filename

        //Get the image from the imageUri
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        // compressing the image using bitmap
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
        final byte[] data = arrayOutputStream.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);

        //Upload the image to the firebase storage
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Dismiss the loading screen once finished
                dialog.dismiss();

                //Get the download URI of the uploaded image
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString(); // getting url if task is successful

                //Once we get the uri, add the image as a new message in the database
                if (uriTask.isSuccessful()) {
                    //Create a new message
                    Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", myUid);
                    hashMap.put("receiver", uid);
                    hashMap.put("message", downloadUri);
                    hashMap.put("timestamp", timestamp);
                    hashMap.put("type", "images");
                    //Add the message to the database
                    firebaseDatabase.mDatabase.child(Database.CHATS).push().setValue(hashMap);

                    //Create the chatList
                    final DatabaseReference ref1 = firebaseDatabase.mDatabase.child(Database.CHATLIST).child(uid).child(myUid);
                    firebaseDatabase.createConversationRecord(myUid,uid);
                }
            }
        });
    }

    /**
     * Pick a picture from the gallery
     */
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
    }

    /**
     * Check if the app has storage permissions
     * @return
     */
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    /**
     * Request the permissions for the storage
     */
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    /**
     * Send a message
     * @param message
     */
    private void sendMessage(final String message) {
        //Get time
        String timestamp = String.valueOf(System.currentTimeMillis());
        //Create the message with the informations about the sender, receiver ...
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", uid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");
        //Add the message to the database
        firebaseDatabase.mDatabase.child(Database.CHATS).push().setValue(hashMap);
        firebaseDatabase.createConversationRecord(myUid, uid);
        hideKeyboard(this);
    }


    /**
     * get the current user
     */
    private void getCurrentUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        }
    }

    /**
     * Hide the soft keyboard
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}