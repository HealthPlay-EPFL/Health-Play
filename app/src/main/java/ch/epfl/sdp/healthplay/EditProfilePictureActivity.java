package ch.epfl.sdp.healthplay;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.auth.SignedInActivity;
import ch.epfl.sdp.healthplay.database.Database;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfilePictureActivity extends AppCompatActivity {


    private Uri uri;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance(Database.DATABASE_URL).getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private String mUri = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignedInActivity.SetMode(this);
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
        findViewById(R.id.change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.launch(photoPickerIntent);
            }
        });

        getImage();
    }

    public void exitProfilePicture(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    private void getImage() {
        mDatabase.child(Database.USERS).child(mAuth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.getValue() != null){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveProfilePicture(View view) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Set profile");
        dialog.setMessage("Setting up profile");
        dialog.show();

        if(uri != null) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                StorageTask mtask = mStorage.child("profilePicture").child(user.getUid() + "jpg").putFile(uri);
                mtask.continueWithTask(new Continuation() {
                   @Override
                   public Object then(@NonNull Task task) throws Exception {
                       if(!task.isSuccessful()) {
                           throw task.getException();
                       }
                       else {
                           return  mStorage.child("profilePicture").child(user.getUid() + "jpg").getDownloadUrl();
                       }
                   }
               }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if(task.isSuccessful()) {
                    Uri dlUri = (Uri)task.getResult();
                    mUri = dlUri.toString();

                  //  HashMap<String, Object> userMap = new HashMap<>();
                   // userMap.put("image", mUri);
                    mDatabase.child(Database.USERS).child(mAuth.getCurrentUser().getUid()).child("image").setValue(mUri);

                    dialog.dismiss();
                }
               });
            }

        }

        else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }

    }



}





