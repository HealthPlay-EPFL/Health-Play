package ch.epfl.sdp.healthplay.scan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Optional;

import ch.epfl.sdp.healthplay.BarcodeInformationActivity;
import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.model.Product;

public class AdditionalProductInformationActivity extends AppCompatActivity {

    private Product p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_product_information);

        Intent intent = getIntent();
        String productString = intent.getStringExtra(BarcodeInformationActivity.EXTRA_MESSAGE);
        Optional<Product> product = Product.of(productString);
        product.ifPresent(value -> p = value);

        if (p != null) {
            TextView pGenericName = findViewById(R.id.pGenericNameAdditional);
            pGenericName.setText(p.getGenericName());

            TextView pName = findViewById(R.id.pNameAdditional);
            pName.setText(p.getName());

            TextView ingredients = findViewById(R.id.ingredientsText);
            ingredients.setText(p.getIngredients());

            TextView allergies = findViewById(R.id.allergiesText);
            allergies.setText(p.getAllergens());

            ImageView image = findViewById(R.id.imageNovascore);
            int imageRes = p.getNova().getRes();
            image.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(),
                    imageRes));
        }
    }
}