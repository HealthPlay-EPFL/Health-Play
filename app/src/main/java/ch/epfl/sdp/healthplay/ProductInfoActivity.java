package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.sdp.healthplay.auth.SignedInFragment;

public class ProductInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SignedInFragment.SetMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info3);
    }

    public void sendBarcode(View view) {
        Intent intent = new Intent(this, BarcodeInformationActivity.class);
        EditText barText = findViewById(R.id.barcodeText);
        String message = barText.getText().toString();
        intent.putExtra(BarcodeInformationActivity.EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}