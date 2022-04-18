package ch.epfl.sdp.healthplay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

import ch.epfl.sdp.healthplay.auth.ProfileActivity;
import ch.epfl.sdp.healthplay.auth.ProfileFragment;
import ch.epfl.sdp.healthplay.auth.SignedInActivity;
import ch.epfl.sdp.healthplay.database.Database;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * EditProfilePicture is a fragment to change the profile's photo with a personal photo on own phone.
 */
public class EditProfilePictureFragment extends Fragment {

    private Uri uri;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance(Database.DATABASE_URL).getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private String mUri = "";
    private final static String IMAGE_NOT_SELECTED = "Image not selected";
    private View view;

    public EditProfilePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile_picture, container, false);
        SignedInActivity.SetMode(getContext());
        CircleImageView profilePictureView = view.findViewById(R.id.edit_profile_picture);

        ActivityResultLauncher<Intent> intent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result ->{
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        if(result.getData()!= null) {
                            profilePictureView.setImageURI(result.getData().getData());
                            uri = result.getData().getData();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "try again!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        view.findViewById(R.id.change_button).setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.launch(photoPickerIntent);
        });
        initButton();
        getImage();
        return view;
    }

    private void initButton(){
        view.findViewById(R.id.exit_button).setOnClickListener(v -> {
            exitProfilePicture();
        });
        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            saveProfilePicture();
        });
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
                        ImageView imageView = view.findViewById(R.id.edit_profile_picture);
                        Glide.with(getContext()).load(image).into(imageView);
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
     */
    public void exitProfilePicture() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.fragmentContainerView, new ProfileFragment());
        fragmentTransaction.commit();
    }

    /**
     * Save the profile picture into the database and create a new image field for the user that contains the url to the image
     */
    public void saveProfilePicture() {
        ProgressDialog dialog = new ProgressDialog(getContext());
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
            Toast.makeText(getContext(), IMAGE_NOT_SELECTED, Toast.LENGTH_SHORT).show();
        }

    }



}