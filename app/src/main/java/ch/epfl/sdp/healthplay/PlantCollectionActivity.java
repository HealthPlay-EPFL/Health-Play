package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.healthplay.database.Plant;
import ch.epfl.sdp.healthplay.databinding.ActivityPlantCollectionBinding;

public class PlantCollectionActivity extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_collection);

        List<String> collectionNames = new ArrayList<String>();
        List<String> collectionDates = new ArrayList<String>();
        List<String> collectionImages = new ArrayList<String>();

        collectionNames.add("Coquelicot");
        collectionNames.add("Tulipe");
        collectionNames.add("Orchidee");

        collectionDates.add("1er janvier");
        collectionDates.add("29 février");
        collectionDates.add("47 août");

        collectionImages.add("https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/C15T5QRVPLepZhX9UQvLgcglDGv2%2F20220329_152554_?alt=media&token=3271fc9a-d842-4edd-850d-3e55a6f26e29");
        collectionImages.add("https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/C15T5QRVPLepZhX9UQvLgcglDGv2%2F20220329_152554_?alt=media&token=3271fc9a-d842-4edd-850d-3e55a6f26e29");
        collectionImages.add("https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/C15T5QRVPLepZhX9UQvLgcglDGv2%2F20220329_152554_?alt=media&token=3271fc9a-d842-4edd-850d-3e55a6f26e29");

        ArrayList<Plant> collectionPlants = new ArrayList<Plant>();

        for (int i = 0; i < 3; i++) {
            Plant plant = new Plant(collectionImages.get(i), collectionNames.get(i), collectionDates.get(i));
            collectionPlants.add(plant);
            System.out.println(plant.name);
        }

        ListAdapterPlant listAdapter = new ListAdapterPlant(PlantCollectionActivity.this, collectionPlants);
        list = (ListView)findViewById(R.id.plantCollectionList);
        list.setAdapter(listAdapter);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlantCollectionActivity.this, PlantCollectionItemActivity.class);
                intent.putExtra("name", collectionNames.get(position));
                intent.putExtra("date", collectionDates.get(position));
                intent.putExtra("image", collectionImages.get(position));
                startActivity(intent);
            }
        });

    }
}