package ch.epfl.sdp.healthplay.scan;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;

public final class ScanDecoder {

    private final static Database database = new Database();

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

    public static void scan(Uri uri, Context context,
                            Consumer<String> onProduct, View view) throws IOException {
        InputImage image = InputImage.fromFilePath(context, uri);
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.isEmpty()) {
                        Toast.makeText(view.getContext(),
                                        "An error occurred. Please try again !",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                    for (Barcode barcode : barcodes) {
                        // Get the code from the barcode
                        final String barcodeString;
                        barcodeString = barcode.getRawValue();
                        int barcodeType = barcode.getValueType();
                        switch (barcodeType) {
                            case Barcode.TYPE_PRODUCT:
                                // Here we check if the product exists
                                try {
                                    ProductManager.getProduct(barcodeString).thenAccept(p -> {
                                        if (p.isPresent())
                                            onProduct.accept(p.get().getJsonString());
                                        else
                                            Toast.makeText(view.getContext(),
                                                            "An error occurred. Please try again !",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                    }).get(3, TimeUnit.SECONDS);
                                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                    Toast.makeText(view.getContext(),
                                                    "An error occurred while scanning the product. This might be due to the server not being responsive. Please try again in a few moments.",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                                break;
                            case Barcode.TYPE_TEXT:
                                // This part is for the friends QR code
                                database.mDatabase
                                        .child(Database.USERS)
                                        .child(barcodeString)
                                        .get()
                                        .addOnSuccessListener(dataSnapshot -> {
                                            // Check if the user exists in the database
                                            if (dataSnapshot.exists()) {
                                                // Set the info view to be visible
                                                view.findViewById(R.id.shadowFrameScanFriend).setVisibility(View.VISIBLE);
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                                                assert user != null;
                                                String username = (String) user.getOrDefault(Database.USERNAME, "None");
                                                TextView usernameText = view.findViewById(R.id.userNameScan);
                                                usernameText.setText(username);
                                                String pPicture = (String) user.get("image");
                                                Glide.with(context.getApplicationContext()).load(pPicture).into((ImageView) view.findViewById(R.id.profilePictureAddFriend));

                                                view.findViewById(R.id.addFriendScanButton).setOnClickListener(onClick -> {
                                                    // Add the user to the friend list
                                                    database.addToFriendList(barcodeString);
                                                    // Remove info view
                                                    view.findViewById(R.id.shadowFrameScanFriend).setVisibility(View.GONE);
                                                    Toast.makeText(view.getContext(),
                                                                    username + " was added in your friends list !",
                                                                    Toast.LENGTH_SHORT)
                                                            .show();
                                                });
                                            }
                                        });
                        }
                    }
                });
    }
}
