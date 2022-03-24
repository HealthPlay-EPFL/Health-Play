package ch.epfl.sdp.healthplay;

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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraApi extends AppCompatActivity {

    Button captureButton;
    ImageView displayImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    String fileName;

    private static final String STORAGE_URL = "gs://health-play-9e161.appspot.com";
    public static StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_api);

        captureButton = findViewById(R.id.buttonCapture);
        displayImage = findViewById(R.id.imageDisplay);

        storage = FirebaseStorage.getInstance(STORAGE_URL).getReference();

        //Start camera intent when clicking on Take Picture button
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraIntent();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Add picture to Firebase
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath,bmOptions);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
            storage.child(fileName).putBytes(outputStream.toByteArray()).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("File uploaded successfully!");
                        }
                    }
            );
        }


        //Display picture in thumbnail
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            displayImage.setImageBitmap(bitmap);
        }*/
    }

    //Define File storing the picture, transition to camera and add picture to app gallery
    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "ch.epfl.sdp.healthplay.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            galleryAddPic();
            //storageAddPic();
            /*storage.child(fileName).putBytes(takePictureIntent.getByteArrayExtra(MediaStore.EXTRA_OUTPUT)).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("File uploaded successfully!");
                        }
                    }
            );*/
            System.out.println(currentPhotoPath);
        }
    }

    //Initializes File storing the picture
    private File createImageFile() throws IOException {
        //Defines name for picture based on date
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = timeStamp + "_";

        //Creates temporary File in app gallery
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );

        //Stores path to current picture in currentPhotoPath
        currentPhotoPath = image.getPath();
        return image;
    }

    //Adds picture to app gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //Adds picture to app gallery
    private void storageAddPic() {
        /*storage.child(fileName).putBytes(mediaScanIntent.getByteArrayExtra(MediaStore.EXTRA_OUTPUT)).addOnSuccessListener(
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("File uploaded successfully!");
                    }
                }
        );*/
    }


}