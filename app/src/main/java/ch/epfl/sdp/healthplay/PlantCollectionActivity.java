package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.database.Plant;
import ch.epfl.sdp.healthplay.databinding.ActivityPlantCollectionBinding;

public class PlantCollectionActivity extends AppCompatActivity {

    ActivityPlantCollectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlantCollectionBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_plant_collection);

        List<String> collectionNames = new ArrayList<String>();
        List<String> collectionDates = new ArrayList<String>();

        collectionNames.add("Coquelicot");
        collectionNames.add("Tulipe");
        collectionNames.add("Orchidee");

        collectionDates.add("1er janvier");
        collectionDates.add("29 février");
        collectionDates.add("47 août");

        ArrayList<Plant> collectionPlants = new ArrayList<Plant>();

        for (int i = 0; i < 3; i++) {
            Plant plant = new Plant("qsqkjfskfqsf", collectionNames.get(i), collectionDates.get(i), R.drawable.coquelicot);
            collectionPlants.add(plant);
            System.out.println(plant.name);
        }

        ListAdapterPlant listAdapter = new ListAdapterPlant(PlantCollectionActivity.this, collectionPlants);

        binding.plantCollectionList.setAdapter(listAdapter);
        binding.plantCollectionList.setClickable(true);
        binding.plantCollectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(PlantCollectionActivity.this, PlantCollectionItemActivity.class);
                i.putExtra("name", collectionNames.get(position));
                i.putExtra("date", collectionDates.get(position));
                i.putExtra("image", "https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/C15T5QRVPLepZhX9UQvLgcglDGv2%2F20220329_152554_?alt=media&token=3271fc9a-d842-4edd-850d-3e55a6f26e29");
                startActivity(i);
            }
        });

    }
}