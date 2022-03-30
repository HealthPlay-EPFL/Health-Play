package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import ch.epfl.sdp.healthplay.databinding.ActivityPlantCollectionItemBinding;

public class PlantCollectionItemActivity extends AppCompatActivity {

    ActivityPlantCollectionItemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlantCollectionItemBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_plant_collection_item);

        Intent intent = this.getIntent();

        if (intent != null){
            String name = intent.getStringExtra("name");
            String date = intent.getStringExtra("date");
            String image = intent.getStringExtra("image");

            binding.plantItemName.setText(name);
            binding.plantItemDate.setText(date);
            Glide.with(this).load(image).into(binding.plantItemView);
        }
    }
}