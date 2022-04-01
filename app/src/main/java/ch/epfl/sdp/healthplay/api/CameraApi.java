package ch.epfl.sdp.healthplay.api;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraApi {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String URL_FIRST = "https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/Planthunt%2F";
    private static final String URL_SECOND = "?alt=media&token=937922cf-0744-4718-8ecf-c1abdda627c8";


    //Initializes File storing the picture
    public static File createImageFile(Context context) throws IOException {
        //Defines name for picture based on date
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = timeStamp + "_";

        //Creates temporary File in app gallery
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );


        return image;
    }

    public static String getImageUrl(FirebaseUser user, String fileName){
        return URL_FIRST + user.getUid() + "%2F" + fileName + URL_SECOND;
    }
}
