package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.net.URI;

public class BarcodeScanActivity extends AppCompatActivity {

    // This field is only used in tests
    protected final static CountingIdlingResource idlingResource =
            new CountingIdlingResource("LOOKUP_BARCODE");

    // The barcode formats used
    private final static BarcodeScannerOptions options =
            new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                            Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_EAN_8)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);
    }

    public void onClick(View view) {
        idlingResource.increment();

        // Set the progress bar to visible and disable user interaction
        ProgressBar bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // TODO: Hardcoded Uri's., will be changed to allow user to input their own images.
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/"+R.drawable.barcode_example);

        InputImage image;
        try {
            scan(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Take out progress bar and clear not touchable flags
        bar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        idlingResource.decrement();
    }

    private void scan(Uri uri) throws IOException {
        InputImage image;
        image = InputImage.fromFilePath(getApplicationContext(), uri);
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
                    Intent intent = new Intent(this, BarcodeInformationActivity.class);
                    // Get the code from the barcode
                    String barcodeString;

                    for (Barcode barcode : barcodes) {
                        barcodeString = barcode.getRawValue();
                        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, barcodeString);
                    }

                    // Go to barcode information activity
                    startActivity(intent);
                });
    }

    public void enterManually(View view) {
        Intent intent = new Intent(this, ProductInfoActivity.class);
        startActivity(intent);
    }
}