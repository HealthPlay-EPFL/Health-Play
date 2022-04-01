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
    /**
     * Overwrites the default ArrayAdapter to return a collection item from
     * a Plant data extracted from the user collection in the database
     **/
    public ListAdapterPlant(Context context, ArrayList<Plant> plantList){
        super(context, R.layout.item_plant_collection, plantList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Plant plant = getItem(position);

        //Check conversion possibility
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_plant_collection, parent, false);
        }

        //Define collection item XML elements
        ImageView plantImage = convertView.findViewById(R.id.plantListImage);
        TextView plantName = convertView.findViewById(R.id.plantListName);
        TextView plantDate = convertView.findViewById(R.id.plantListDate);

        //Set collection item XML elements with Plant elements
        plantName.setText(plant.GetName());
        plantDate.setText(plant.GetDate());
        Glide.with(getContext()).load(plant.GetImagePath()).into(plantImage);

        return convertView;
    }
}
