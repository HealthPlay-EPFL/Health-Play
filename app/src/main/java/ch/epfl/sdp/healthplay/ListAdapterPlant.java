package ch.epfl.sdp.healthplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ch.epfl.sdp.healthplay.database.Plant;

public class ListAdapterPlant extends ArrayAdapter<Plant> {

    public ListAdapterPlant(Context context, ArrayList<Plant> plantList){
        super(context, R.layout.item_plant_collection, plantList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Plant plant = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_plant_collection, parent, false);
        }

        //ImageView plantView = convertView.findViewById(R.id.plantPictureView);
        TextView plantName = convertView.findViewById(R.id.plantName);
        TextView plantDate = convertView.findViewById(R.id.plantDate);

        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/C15T5QRVPLepZhX9UQvLgcglDGv2%2F20220329_152554_?alt=media&token=3271fc9a-d842-4edd-850d-3e55a6f26e29").into(plantView);
        plantName.setText(plant.name);
        plantDate.setText(plant.date);
        //plantView.setImageResource(plant.imageId);
        //Glide.with(this).load(plant.imagePath).into(binding.plantItemView);
        System.out.println(plant.date);

        return convertView;
    }
}
