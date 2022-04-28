package ch.epfl.sdp.healthplay;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import ch.epfl.sdp.healthplay.database.Database;
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

        // Get the authenticated user if any
        user = FirebaseAuth.getInstance().getCurrentUser();

        TextView pName = findViewById(R.id.pName);
        pName.setText(productName.get());

        TextView pEnergy = findViewById(R.id.pEnergy);
        pEnergy.setText(energy.get());

        if (p != null) {
            TextView pGenericName = findViewById(R.id.pGenericName);
            pGenericName.setText(p.getGenericName());
            LinearLayout parentLayout = findViewById(R.id.informationLayout);

            for (Product.Nutriments nutriments: Product.Nutriments.values()) {

                double serving = p.getNutrimentServing(nutriments);
                if (serving > 0) {
                    // Create new layout for the new fields
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    parentLayout.addView(linearLayout);

                    // Get the text of the nutriment
                    TextView textView1 = new TextView(this);
                    String name = nutriments.getName();
                    String nameToDisplay = name.substring(0, 1).toUpperCase() + name.substring(1);
                    textView1.setText(String.format("%s:", nameToDisplay));
                    textView1.setTextSize(20);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    // Trick to change dp to px as this function takes in pixels as arguments
                    params.setMargins((int) (10 * Resources.getSystem().getDisplayMetrics().density), 0, 0, 0);

                    textView1.setLayoutParams(params);

                    linearLayout.addView(textView1);

                    TextView textView2 = new TextView(this);
                    textView2.setText(String.format(
                            Double.toString(p.getNutrimentServing(nutriments)), Locale.ENGLISH));
                    textView2.setTextSize(20);
                    textView2.setLayoutParams(params);
                    // Set the tag of the textview to find it later
                    textView2.setTag(nutriments.getName());

                    linearLayout.addView(textView2);

                    // Create the switch
                    SwitchCompat s = new SwitchCompat(this);
                    s.setText("");
                    s.setTag(nutriments.getName()+"_switch");
                    LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1
                    );
                    s.setPaddingRelative(0, 0, (int) (10 * Resources.getSystem().getDisplayMetrics().density), 0);
                    s.setLayoutParams(switchParams);
                    s.setChecked(true);

                    // If the user is logged in, allow the choice
                    if (user != null)
                        linearLayout.addView(s);
                }
            }
        }

        // Check if a user is logged in and allow them to save the calorie counter
        if (user != null) {
            findViewById(R.id.incr_button).setVisibility(View.VISIBLE);
            findViewById(R.id.incr_button2).setVisibility(View.VISIBLE);
            findViewById(R.id.quantity_name).setVisibility(View.VISIBLE);
            findViewById(R.id.quantity_name2).setVisibility(View.VISIBLE);
            findViewById(R.id.add_to_counter_button).setVisibility(View.VISIBLE);
        }
    }

    private void changeValue(boolean divide) {
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
            for (Product.Nutriments nutriments: Product.Nutriments.values()) {
                double serving = p.getNutrimentServing(nutriments);
                if (serving > 0) {
                    textView = findViewById(R.id.informationLayout).findViewWithTag(nutriments.getName());
                    calorieText = textView.getText().toString();
                    newCalorie = Double.parseDouble(calorieText) / 2.0;
                    textView.setText(String.format(Double.toString(newCalorie), Locale.ENGLISH));
                }
            }
        } else if (!divide) {
            newCalorie = 2.0 * Double.parseDouble(calorieText);
            counter.setText(String.format(Integer.toString(quantity + 1), Locale.ENGLISH));
            String newText = String.format(Double.toString(newCalorie), Locale.ENGLISH);
            textView.setText(newText);
            for (Product.Nutriments nutriments: Product.Nutriments.values()) {
                double serving = p.getNutrimentServing(nutriments);
                if (serving > 0) {
                    textView = findViewById(R.id.informationLayout).findViewWithTag(nutriments.getName());
                    calorieText = textView.getText().toString();
                    newCalorie = Double.parseDouble(calorieText) * 2.0;
                    textView.setText(String.format(Double.toString(newCalorie), Locale.ENGLISH));
                }
            }
        }

    }

    public void increment(View view) {
        changeValue(false);
    }

    public void decrement(View view) {
        changeValue(true);
    }

    public void addToUser(View view) {
        Database db = new Database();
        TextView textView = findViewById(R.id.pEnergy);
        double calorie = Double.parseDouble(textView.getText().toString());
        if (((Switch) findViewById(R.id.pEnergyUnitSwitch)).isChecked())
            db.addCalorie(user.getUid(), (int) calorie);

        for (Product.Nutriments nutriments: Product.Nutriments.values()) {
            double serving = p.getNutrimentServing(nutriments);
            if (serving > 0) {
                if (((SwitchCompat) findViewById(R.id.informationLayout).findViewWithTag(nutriments.getName()+"_switch")).isChecked())
                    db.addNutrimentField(user.getUid(), nutriments, serving);
            }
        }

        Toast t = Toast.makeText(getApplicationContext(), "Information saved on the profile !", Toast.LENGTH_SHORT);
        t.show();
    }
}
