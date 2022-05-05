package ch.epfl.sdp.healthplay.planthunt;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.api.CameraApi;
import ch.epfl.sdp.healthplay.api.PlantnetApi;
import ch.epfl.sdp.healthplay.database.Database;

public class PlanthuntLobbyActivity extends AppCompatActivity {


    private final Database db = new Database();
    private String lobbyName, currentUsername, hostStatus;
    private static int remainingTime = 300;

    private File photoFile;
    private static final String STORAGE_URL = "gs://health-play-9e161.appspot.com";
    public static StorageReference storage;
    private FirebaseUser user;
    public static final String URL = "URL";
    public static final String NAME = "NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_lobby);

        Intent intent = getIntent();
        lobbyName = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME);
        currentUsername = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.USERNAME);
        hostStatus = intent.getStringExtra(PlanthuntCreateJoinLobbyActivity.HOST);

        final TextView lobbyNameText = findViewById(R.id.planthuntLobbyName);
        lobbyNameText.setText(lobbyName);

        final TextView lobbyTimeText = findViewById(R.id.planthuntLobbyTimeText);
        final ProgressBar lobbyTimeBar = findViewById(R.id.planthuntLobbyTimeBar);

        final TextView lobbyScore = findViewById(R.id.planthuntLobbyScore);

        //Get current user reference in Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Get Firebase Storage from Url
        storage = FirebaseStorage.getInstance(STORAGE_URL).getReference();

        final Button captureButton = findViewById(R.id.planthuntLobbyButton);

        //Start camera intent when clicking on Take Picture button
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraIntent();
            }
        });

        if (remainingTime == 300 && hostStatus.equals("host")){
            startTimer();
        }

        db.mDatabase.child(Database.LOBBIES).child(lobbyName).child(Database.REMAINING_TIME).addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int time = Math.toIntExact((long) snapshot.getValue());
                        String first = round(time / 60) < 10
                                ? "0" + round(time / 60)
                                : Integer.toString((round(time / 60)));
                        String second = time % 60 < 10
                                ? "0" + time % 60
                                : Integer.toString(time % 60);
                        lobbyTimeText.setText(first + ":" + second);
                        lobbyTimeBar.setProgress(time);
                        if (time == 0){
                            Intent intent = new Intent(PlanthuntLobbyActivity.this, PlanthuntResultActivity.class);
                            intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.toString());
                    }
                }

        );

        db.mDatabase.child(Database.LOBBIES).child(lobbyName).addValueEventListener(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        db.getLobbyPlayerScore(lobbyName, currentUsername, task -> {
                            if (!task.isSuccessful()) {

                                Log.e("ERROR", "An error happened");
                            }
                            lobbyScore.setText("Score: " + Objects.requireNonNull(task.getResult().getValue()));
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.toString());
                    }
                }
        );
    }

    private void startTimer(){
        //Creates a new Thread to update timer asynchronously
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 301; i++) {
                    try {
                        Thread.sleep(1000);
                        db.updateLobbyTime(lobbyName, remainingTime);
                        remainingTime--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    //Define File storing the picture, transition to camera and add picture to app gallery
    public void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = null;
        try {
            photoFile = CameraApi.createImageFile(this);
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "ch.epfl.sdp.healthplay.fileprovider",
                    photoFile);
            //Add data to intent result
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, CameraApi.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check request comes from camera
        if (requestCode == CameraApi.REQUEST_IMAGE_CAPTURE){
            //Convert image to byte array then to jpeg
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(),bmOptions);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            //Send image Storage Url on Firebase to Plantnet activity
            Intent intent = new Intent(this, PlanthuntNewPlantActivity.class);
            intent.putExtra(PlanthuntCreateJoinLobbyActivity.LOBBY_NAME, lobbyName);
            intent.putExtra(PlanthuntCreateJoinLobbyActivity.USERNAME, currentUsername);
            String urlImage = CameraApi.getImageUrl(user, photoFile.getName());

            //Add picture to Firebase
            storage.child("Planthunt").child(user.getUid()).child(photoFile.getName()).putBytes(outputStream.toByteArray()).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Creates a new Thread to receive Url response asynchronously
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try  {
                                        //Returns built URL with given image link
                                        String urlString = PlantnetApi.buildUrl(PlantnetApi.API_KEY, urlImage, "flower");

                                        //Gets JSON object from built URL
                                        JSONObject json = PlantnetApi.readJsonFromUrl(urlString);

                                        //Extracts plant name from received JSON
                                        String commonName = json.getJSONArray("results")
                                                .getJSONObject(0)
                                                .getJSONObject("species")
                                                .getJSONArray("commonNames")
                                                .get(0)
                                                .toString();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                intent.putExtra(NAME, commonName);
                                                storage.child("Planthunt").child(user.getUid()).child(photoFile.getName()).delete();
                                                storage.child("Planthunt").child(user.getUid()).child(commonName + "_" + photoFile.getName()).putBytes(outputStream.toByteArray());
                                                intent.putExtra(URL, CameraApi.getNewImageUrl(user, photoFile.getName(), commonName));
                                                startActivity(intent);
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                    }
            );
        }
    }
}