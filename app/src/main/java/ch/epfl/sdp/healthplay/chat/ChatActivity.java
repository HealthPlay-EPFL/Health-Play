package ch.epfl.sdp.healthplay.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.view.MenuItem;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profile, block;
    TextView name, userStatus;
    EditText msg;
    ImageButton send, attach;
    FirebaseAuth firebaseAuth;
    String uid, myUid, image;
    ValueEventListener valueEventListener;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int STORAGE_REQUEST = 200;
    private String[] storagePermission;
    private Uri imageUri = null;
    private Database firebaseDatabase;
    boolean notify = false;
    boolean isBlocked = false;

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
        block = findViewById(R.id.block);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = findViewById(R.id.chatrecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        uid = getIntent().getStringExtra("uid");

        // initialize database
        firebaseDatabase = new Database();

        // initialising permissions
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        checkUserStatus();
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = msg.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {//if empty
                    Toast.makeText(ChatActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
                } else {
                    sendMessage(message);
                }
                msg.setText("");
                checkTypingStatus("noOne");
            }
        });

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTypingStatus(uid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        firebaseDatabase.mDatabase.child(Database.USERS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // retrieve user data
                    String username = (String)dataSnapshot.child(Database.USERNAME).getValue();
                    image = (String) dataSnapshot.child("image").getValue();
                    String onlineStatus = (String) dataSnapshot.child("onlineStatus").getValue();
                    String typingTo = (String) dataSnapshot.child("typingTo").getValue();

                    if (typingTo.equals(myUid)) {// if user is typing to my chat
                        userStatus.setText("Typing....");// type status as typing
                    } else {
                        if (onlineStatus.equals("online")) {
                            userStatus.setText(onlineStatus);
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                            String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                            userStatus.setText("Last Seen:" + timedate);
                        }
                    }
                    name.setText(username);
                    try {
                        Glide.with(ChatActivity.this).load(image).placeholder(R.drawable.profile_icon).into(profile);
                    } catch (Exception e) {

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        readMessages();
    }


    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkOnlineStatus(String status) {
        // check online status
        DatabaseReference dbref = firebaseDatabase.mDatabase.child(Database.USERS).child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);
        dbref.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing) {
        DatabaseReference dbref = firebaseDatabase.mDatabase.child(Database.USERS).child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbref.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    private void readMessages() {
        // show message after retrieving data
        chatList = new ArrayList<>();
        DatabaseReference dbref = firebaseDatabase.mDatabase.child("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat modelChat = dataSnapshot1.getValue(ModelChat.class);
                    if (modelChat.getSender().equals(myUid) &&
                            modelChat.getReceiver().equals(uid) ||
                            modelChat.getReceiver().equals(myUid)
                                    && modelChat.getSender().equals(uid)) {
                        chatList.add(modelChat); // add the chat in chatlist
                    }
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

    private void showImagePicDialog() {
        String[] options = {"Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("CHECK PERMISSIONS", String.valueOf(checkStoragePermission()));
                if (!checkStoragePermission()) { // if permission is not given
                        requestStoragePermission(); // request for permission
                    } else {
                        pickFromGallery(); // if already access granted then pick
                    }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // request for permission if not given
        Log.e("REQUEST CODE", String.valueOf(requestCode));
            if(requestCode == STORAGE_REQUEST){
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    /*if (writeStorageAccepted) {
                        pickFromGallery(); // if access granted then pick
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }*/
                    pickFromGallery(); // if access granted then pick
                }
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageUri = data.getData(); // get image data to upload
                try {
                    sendImageMessage(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImageMessage(Uri imageUri) throws IOException {
        notify = true;
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image");
        dialog.show();

        // If we are sending image as a message
        // then we need to find the url of
        // image after uploading the
        // image in firebase storage
        final String timestamp = "" + System.currentTimeMillis();
        String filePathAndName = "ChatImages/" + "post" + timestamp; // filename
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream); // compressing the image using bitmap
        final byte[] data = arrayOutputStream.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString(); // getting url if task is successful

                if (uriTask.isSuccessful()) {
                    DatabaseReference re = firebaseDatabase.mDatabase;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", myUid);
                    hashMap.put("receiver", uid);
                    hashMap.put("message", downloadUri);
                    hashMap.put("timestamp", timestamp);
                    hashMap.put("dilihat", false);
                    hashMap.put("type", "images");
                    re.child("Chats").push().setValue(hashMap); // push in firebase using unique id
                    final DatabaseReference ref1 = firebaseDatabase.mDatabase.child("ChatList").child(uid).child(myUid);
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                ref1.child("id").setValue(myUid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    final DatabaseReference ref2 = firebaseDatabase.mDatabase.child("ChatList").child(myUid).child(uid);
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                ref2.child("id").setValue(uid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
    }

    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private void sendMessage(final String message) {
        // creating a reference to store data in firebase
        // We will be storing data using current time in "Chatlist"
        // and we are pushing data using unique id in "Chats"
        DatabaseReference databaseReference = firebaseDatabase.mDatabase;
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", uid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("dilihat", false);
        hashMap.put("type", "text");
        databaseReference.child("Chats").push().setValue(hashMap);
        final DatabaseReference ref1 = firebaseDatabase.mDatabase.child("ChatList").child(uid).child(myUid);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref1.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference ref2 = firebaseDatabase.mDatabase.child("ChatList").child(myUid).child(uid);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    ref2.child("id").setValue(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        }
    }
}