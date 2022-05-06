package ch.epfl.sdp.healthplay;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.healthplay.database.Database;

public class BarcodeScanActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private final Database database = new Database();

    // This field is only used in tests
    protected final static CountingIdlingResource idlingResource =
            new CountingIdlingResource("LOOKUP_BARCODE");

    // The barcode formats used
    private final static BarcodeScannerOptions options =
            new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                            // Allow scanning of product barcodes
                            Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_EAN_8,
                            // Allow scanning of QR codes
                            Barcode.FORMAT_QR_CODE)
                    .build();
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
        previewView = findViewById(R.id.previewView2);

        // Get the camera provider of the device
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Used to create a camera and bind the preview
     */
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        // Unbind everything that was bind to this camera
        cameraProvider.unbindAll();

        // The preview on the screen
        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Create the image capture use case to use to take a photo
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Create the camera
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                imageCapture,
                preview);
    }

    public void onClick(View view) throws IOException {
        idlingResource.increment();
        // Set the progress bar to visible and disable user interaction
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        File file = File.createTempFile("barcode_image", ".jpeg", this.getCacheDir());

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri uri = outputFileResults.getSavedUri();

                try {
                    scan(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Take out progress bar and clear not touchable flags
                bar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                boolean ignored = file.delete();
                idlingResource.decrement();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // Take out progress bar and clear not touchable flags
                boolean ignored = file.delete();
                bar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                idlingResource.decrement();
            }
        });
    }

    private void scan(Uri uri) throws IOException {
        InputImage image = InputImage.fromFilePath(getApplicationContext(), uri);
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.isEmpty()) {
                        // Popup dialog whenever an error occurs
                        new AlertDialog.Builder(BarcodeScanActivity.this)
                                .setTitle("Error")
                                .setMessage("An error occurred. Please try again")
                                .setNeutralButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }

                    for (Barcode barcode : barcodes) {
                        // Get the code from the barcode
                        final String barcodeString;
                        barcodeString = barcode.getRawValue();
                        int barcodeType = barcode.getValueType();
                        switch (barcodeType) {
                            case Barcode.FORMAT_EAN_13:
                            case Barcode.FORMAT_EAN_8:
                                Intent intent = new Intent(this, BarcodeInformationActivity.class);
                                intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, barcodeString);
                                // Go to barcode information activity
                                startActivity(intent);
                                break;
                            case Barcode.FORMAT_QR_CODE:
                                // This part is for the friends QR code

                                database.mDatabase
                                        .child(Database.USERS)
                                        .child(barcodeString)
                                        .get()
                                        .addOnSuccessListener(dataSnapshot -> {
                                            // Check if the user exists in the database
                                            if (dataSnapshot.exists()) {
                                                // Set the info view to be visible
                                                findViewById(R.id.shadowFrameScanFriend).setVisibility(View.VISIBLE);
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                                                assert user != null;
                                                String username = (String) user.getOrDefault(Database.USERNAME, "None");
                                                TextView usernameText = findViewById(R.id.userNameScan);
                                                usernameText.setText(username);
                                                String pPicture = (String) user.get("image");
                                                Glide.with(getApplicationContext()).load(pPicture).into((ImageView) findViewById(R.id.profilePictureAddFriend));

                                                findViewById(R.id.addFriendScanButton).setOnClickListener(onClick -> {
                                                    // Add the user to the friend list
                                                    database.addToFriendList(barcodeString);
                                                    // Remove info view
                                                    findViewById(R.id.shadowFrameScanFriend).setVisibility(View.GONE);
                                                });
                                            }
                                        });
                                break;
                        }

                    }
                });
    }

    public void enterManually(View view) {
        Intent intent = new Intent(this, ProductInfoActivity.class);
        startActivity(intent);
        finish();
    }
}