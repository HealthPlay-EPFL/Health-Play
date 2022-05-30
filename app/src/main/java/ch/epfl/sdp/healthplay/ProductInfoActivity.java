package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.healthplay.auth.SignedInFragment;
import ch.epfl.sdp.healthplay.scan.ProductManager;

public class ProductInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignedInFragment.SetMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info3);
    }

    public void sendBarcode(View view) {
        ProgressBar b = findViewById(R.id.progressBar2);
        b.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, BarcodeInformationActivity.class);
        EditText barText = findViewById(R.id.barcodeText);
        String message = barText.getText().toString();
        ProductManager
                .getProduct(message)
                .thenAccept(p -> {
                    if (p.isPresent()) {
                        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, p.get().getJsonString());
                        startActivity(intent);
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(),
                                    // TODO: Enable translations
                                    "Sorry, the barcode you have entered does not exist on the server, or we are unable to join the server at the moment.",
                                    Toast.LENGTH_LONG).show();
                            barText.setText("");
                        });
                    }
                    b.setVisibility(View.GONE);
                });

    }
}