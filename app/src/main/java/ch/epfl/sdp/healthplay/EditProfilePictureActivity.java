package ch.epfl.sdp.healthplay;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.InputStream;
import java.util.HashMap;

import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.auth.SignedInFragment;
import ch.epfl.sdp.healthplay.database.Database;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfilePictureActivity extends AppCompatActivity {

    private  Uri uri;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance(Database.DATABASE_URL).getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private String mUri = "";
    private final static String IMAGE_NOT_SELECTED = "Image not selected";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignedInFragment.SetMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_picture);
        CircleImageView profilePictureView = findViewById(R.id.edit_profile_picture);

        ActivityResultLauncher<Intent> intent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result ->{
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        if(result.getData()!= null) {
                            profilePictureView.setImageURI(result.getData().getData());
                            uri = result.getData().getData();
                        }
                    }
                    else {
                        Toast.makeText(this, "try again!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        findViewById(R.id.change_button).setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.launch(photoPickerIntent);
        });

        getImage();

    }


    /**
     * Check for changes in image field of the user and change the profile picture of this activity appropriately
     */
    private void getImage() {
        mDatabase.child(Database.USERS).child(mAuth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.getValue() != null){
                        String image = snapshot.getValue().toString();
                        ImageView imageView = findViewById(R.id.edit_profile_picture);
                        Glide.with(getApplicationContext()).load(image).into(imageView);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * On click method for the exit button
     * @param view
     */
    public void exitProfilePicture(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        finish();
        startActivity(intent);
    }

    /**
     * Save the profile picture into the database and create a new image field for the user that contains the url to the image
     * @param view
     */
    public void saveProfilePicture(View view) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Set profile");
        dialog.setMessage("Setting up profile");
        dialog.show();

        if(uri != null) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                StorageTask mtask = mStorage.child("profilePicture").child(user.getUid() + ".jpg").putFile(uri);
                mtask.continueWithTask(task -> {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    else {
                        return  mStorage.child("profilePicture").child(user.getUid() + ".jpg").getDownloadUrl();
                    }
                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if(task.isSuccessful()) {
                    mUri = ((Uri)task.getResult()).toString();
                    mDatabase.child(Database.USERS).child(mAuth.getCurrentUser().getUid()).child("image").setValue(mUri);
                    dialog.dismiss();
                }
               });
            }

        }
        else {
            Toast.makeText(this, IMAGE_NOT_SELECTED, Toast.LENGTH_SHORT).show();
        }

    }



}





