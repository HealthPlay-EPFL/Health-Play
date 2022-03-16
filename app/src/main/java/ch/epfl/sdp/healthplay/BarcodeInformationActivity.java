package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import ch.epfl.sdp.healthplay.database.User;
import ch.epfl.sdp.healthplay.model.Product;
import ch.epfl.sdp.healthplay.model.ProductInfoClient;

public class BarcodeInformationActivity extends AppCompatActivity {

    private final AtomicReference<String> productName = new AtomicReference<>("Unknown");
    private final AtomicReference<String> energy = new AtomicReference<>("Unknown");
    private FirebaseUser user;

    private Thread pullInformation(String barcode) {
        return new Thread(() -> {
            try {
                ProductInfoClient client = new ProductInfoClient(barcode);
                Optional<Product> p = Product.of(client.getInfo());
                if (p.isPresent()) {
                    Product product = p.get();
                    productName.set(product.getName());
                    int value = product.getKCalEnergy();
                    energy.set(value < 0 ? "Unknown" : Integer.toString(value));
                }

            } catch (IOException ignored) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_information);

        Intent intent = getIntent();
        String barcode = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Thread t = pullInformation(barcode);
        t.start();
        try {
            t.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        TextView pName = findViewById(R.id.pName);
        pName.setText(productName.get());

        TextView pEnergy = findViewById(R.id.pEnergy);
        pEnergy.setText(energy.get());

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if a user is logged in and allow them to save the calorie counter
        if (user != null) {
            findViewById(R.id.incr_button).setVisibility(View.VISIBLE);
            findViewById(R.id.incr_button2).setVisibility(View.VISIBLE);
            findViewById(R.id.quantity_name).setVisibility(View.VISIBLE);
            findViewById(R.id.quantity_name2).setVisibility(View.VISIBLE);
            findViewById(R.id.add_to_counter_button).setVisibility(View.VISIBLE);
        }
    }

    private void changeCalorieText(boolean divide) {
        TextView textView = findViewById(R.id.pEnergy);
        String calorieText = ((TextView) textView).getText().toString();
        double newCalorie = divide ?
                Integer.parseInt(calorieText) / 2.0 :
                2.0 * Integer.parseInt(calorieText);
        String newText = String.format(Double.toString(newCalorie), Locale.UK);
        textView.setText(newText);
    }

    public void increment(View view) {
        changeCalorieText(false);
    }

    public void decrement(View view) {
        changeCalorieText(true);
    }

    public void addToUser(View view) {
        TextView textView = findViewById(R.id.pEnergy);
        double calorie = Integer.parseInt(textView.getText().toString());
        User.addCalorie(user.getUid(), (int) calorie);
    }
}
