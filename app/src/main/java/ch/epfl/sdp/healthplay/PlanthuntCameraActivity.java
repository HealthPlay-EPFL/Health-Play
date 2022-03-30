package ch.epfl.sdp.healthplay;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.sdp.healthplay.api.CameraApi;
import ch.epfl.sdp.healthplay.api.PlantnetApi;

public class PlanthuntCameraActivity extends AppCompatActivity {

    private static Button collectionButton;
    private static Button captureButton;
    private File photoFile;

    private static final String STORAGE_URL = "gs://health-play-9e161.appspot.com";
    public static StorageReference storage;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planhunt_camera);

        captureButton = findViewById(R.id.buttonCapture);
        collectionButton = findViewById(R.id.buttonCollection);

        //Get current user reference in Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Get Firebase Storage from Url
        storage = FirebaseStorage.getInstance(STORAGE_URL).getReference();

        //Start camera intent when clicking on Take Picture button
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraIntent();
            }
        });

        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanthuntCameraActivity.this, PlanthuntCollectionActivity.class);
                startActivity(intent);
            }
        });

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
            Intent intent = new Intent(this, PlanthuntResultActivity.class);
            String urlImage = CameraApi.getImageUrl(user, photoFile.getName());
            intent.putExtra("url", urlImage);

            //Add picture to Firebase
            storage.child("Planthunt").child(user.getUid()).child(photoFile.getName()).putBytes(outputStream.toByteArray()).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("File uploaded successfully!");

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

                                        //Asynchronously outputs extracted name to text field
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                intent.putExtra("name", commonName);
                                                System.out.println(commonName);
                                                storage.child("Planthunt").child(user.getUid()).child(photoFile.getName()).delete();
                                                storage.child("Planthunt").child(user.getUid()).child(commonName + "_" + photoFile.getName()).putBytes(outputStream.toByteArray());
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