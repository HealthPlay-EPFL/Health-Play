package ch.epfl.sdp.healthplay;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.scan.ScanDecoder;

public class BarcodeScanFragment extends Fragment {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private final Database database = new Database();

    // This field is only used in tests
    protected final static CountingIdlingResource idlingResource =
            new CountingIdlingResource("LOOKUP_BARCODE");

    private ImageCapture imageCapture;
    private View view;

    public void createCamera(ProcessCameraProvider cameraProvider, Preview preview) {
        cameraProvider.bindToLifecycle((LifecycleOwner) this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                imageCapture,
                preview);
    }

    public BarcodeScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_barcode_scan, container, false);
        previewView = view.findViewById(R.id.previewView2);

        // Get the camera provider of the device
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(getContext()));
        initButton();
        return view;
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
        createCamera(cameraProvider, preview);
    }

    public void onClick() throws IOException {
        idlingResource.increment();
        // Set the progress bar to visible and disable user interaction
        ProgressBar bar = view.findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        File file = File.createTempFile("barcode_image", ".jpeg", getActivity().getCacheDir());

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getActivity()),
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
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        boolean ignored = file.delete();
                        idlingResource.decrement();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Take out progress bar and clear not touchable flags
                        boolean ignored = file.delete();
                        bar.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        idlingResource.decrement();
                    }
                });
    }

    public void scan(Uri uri) throws IOException {
        ScanDecoder.scan(uri, getActivity().getApplicationContext(),
                productJson -> {
                    // Create the intent for the product information
                    Intent intent = new Intent(getContext(), BarcodeInformationActivity.class);
                    intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, productJson);
                    // Go to barcode information activity
                    startActivity(intent);
                }, getView());
    }

    public void enterManually() {
        Intent intent = new Intent(getContext(), ProductInfoActivity.class);
        startActivity(intent);
    }

    private void initButton() {
        view.findViewById(R.id.get_information_from_barcode).setOnClickListener(v -> {
            try {
                onClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.enter_manually_button).setOnClickListener(v -> enterManually());
    }
}