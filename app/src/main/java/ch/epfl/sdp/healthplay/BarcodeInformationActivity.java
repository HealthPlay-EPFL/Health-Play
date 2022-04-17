package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import ch.epfl.sdp.healthplay.database.Database;
import ch.epfl.sdp.healthplay.database.User;
import ch.epfl.sdp.healthplay.model.Product;
import ch.epfl.sdp.healthplay.model.ProductInfoClient;
//import static ch.epfl.sdp.healthplay.database.Database.INSTANCE;

public class BarcodeInformationActivity extends AppCompatActivity {

    private final AtomicReference<String> productName = new AtomicReference<>(Product.UNKNOWN_NAME);
    private final AtomicReference<String> energy = new AtomicReference<>(Product.UNKNOWN_NAME);
    private Product p;
    private FirebaseUser user;

    public static final String EXTRA_MESSAGE = "ch.epfl.sdp.healthplay.MESSAGE";


    private Thread pullInformation(String barcode) {
        return new Thread(() -> {
            try {
                ProductInfoClient client = new ProductInfoClient(barcode);
                Optional<Product> p = Product.of(client.getInfo());
                if (p.isPresent()) {
                    Product product = p.get();
                    this.p = product;
                    productName.set(product.getName());
                    int value = product.getKCalEnergy();
                    energy.set(value < 0 ? "Unknown" : Integer.toString(value));

                    // Load the image of the product into the view
                    ImageView image = findViewById(R.id.pImage);
                    String imageUrl = product.getImageURL();
                    if (!imageUrl.equals(Product.UNKNOWN_NAME)) {
                        URL url = new URL(product.getImageURL());
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        image.setImageBitmap(bitmap);
                    }

                    image = findViewById(R.id.imageNutriscore);
                    int imageRes = product.getNutriscore().getRes();
                    image.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                            imageRes));
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
        String barcode = intent.getStringExtra(EXTRA_MESSAGE);

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

        TextView pGenericName = findViewById(R.id.pGenericName);
        pGenericName.setText(p.getGenericName());

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
        TextView counter = findViewById(R.id.quantity_name2);
        String calorieText = textView.getText().toString();
        double newCalorie = 0.0;
        int quantity = Integer.parseInt(counter.getText().toString());
        if (Integer.parseInt(counter.getText().toString()) > 1 && divide) {
            newCalorie = Double.parseDouble(calorieText) / 2.0;
            counter.setText(String.format(Integer.toString(quantity - 1), Locale.ENGLISH));
            String newText = String.format(Double.toString(newCalorie), Locale.ENGLISH);
            textView.setText(newText);
        } else if (!divide) {
            newCalorie = 2.0 * Double.parseDouble(calorieText);
            counter.setText(String.format(Integer.toString(quantity + 1), Locale.ENGLISH));
            String newText = String.format(Double.toString(newCalorie), Locale.ENGLISH);
            textView.setText(newText);
        }

    }

    public void increment(View view) {
        changeCalorieText(false);
    }

    public void decrement(View view) {
        changeCalorieText(true);
    }

    public void addToUser(View view) {
        Database db = new Database();
        TextView textView = findViewById(R.id.pEnergy);
        double calorie = Double.parseDouble(textView.getText().toString());
        db.addCalorie(user.getUid(), (int) calorie);
    }
}
