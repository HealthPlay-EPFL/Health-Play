package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ProductInfoActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "ch.epfl.sdp.healthplay.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info3);
    }

    public void sendBarcode(View view) {
        Intent intent = new Intent(this, BarcodeInformationActivity.class);
        EditText barText = findViewById(R.id.barcodeText);
        String message = barText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}