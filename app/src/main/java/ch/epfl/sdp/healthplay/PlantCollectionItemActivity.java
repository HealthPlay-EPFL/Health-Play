package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PlantCollectionItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_collection_item);

        Intent intent = getIntent();
        final TextView plantName = findViewById(R.id.plantItemName);
        final TextView plantDate = findViewById(R.id.plantItemDate);
        final ImageView plantImage = findViewById(R.id.plantItemImage);

        if (intent != null){
            String name = intent.getStringExtra("name");
            String date = intent.getStringExtra("date");
            String image = intent.getStringExtra("image");

            plantName.setText(name);
            plantDate.setText(date);
            Glide.with(this).load(image).into(plantImage);
        }
    }
}