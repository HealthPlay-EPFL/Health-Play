package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sdp.healthplay.api.CameraApi;
import ch.epfl.sdp.healthplay.api.PlantnetApi;
import ch.epfl.sdp.healthplay.database.Plant;

public class PlanthuntCollectionActivity extends AppCompatActivity {

    private static final String STORAGE_URL = "gs://health-play-9e161.appspot.com";
    public static StorageReference storage;
    private static final String uploadedUrlFirst = "https://firebasestorage.googleapis.com/v0/b/health-play-9e161.appspot.com/o/";
    private static final String uploadedUrlSecond = "?alt=media&token=937922cf-0744-4718-8ecf-c1abdda627c8";

    FirebaseUser user;

    ListView listViewPlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planthunt_collection);

        //Get current user reference in Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Get Firebase Storage from Url
        storage = FirebaseStorage.getInstance(STORAGE_URL).getReference();

        List<String> collectionNames = new ArrayList<String>();
        List<String> collectionDates = new ArrayList<String>();
        List<String> collectionImages = new ArrayList<String>();
        ArrayList<Plant> collectionPlants = new ArrayList<Plant>();

        storage.child("Planthunt").child(user.getUid()).listAll().addOnSuccessListener(
                new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            String urlString = CameraApi.getImageUrl(user, item.getName());
                            String[] parts = item.getName().split("_");
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                                Date date = format.parse(parts[1]);
                                collectionDates.add(date.toString().substring(0, 10));
                            } catch (ParseException e) {
                                collectionDates.add("No date found");
                                e.printStackTrace();
                            }
                            collectionNames.add(parts[0]);
                            collectionImages.add(urlString);

                        }
                        for (int i = 0; i < listResult.getItems().size(); i++) {
                            Plant plant = new Plant(collectionImages.get(i), collectionNames.get(i), collectionDates.get(i));
                            collectionPlants.add(plant);
                        }

                        ListAdapterPlant listAdapter = new ListAdapterPlant(PlanthuntCollectionActivity.this, collectionPlants);
                        listViewPlants = (ListView)findViewById(R.id.plantCollectionList);
                        listViewPlants.setAdapter(listAdapter);
                        listViewPlants.setClickable(true);
                        listViewPlants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(PlanthuntCollectionActivity.this, PlanthuntCollectionItemActivity.class);
                                intent.putExtra("name", collectionNames.get(position));
                                intent.putExtra("date", collectionDates.get(position));
                                intent.putExtra("image", collectionImages.get(position));
                                startActivity(intent);
                            }
                        });

                    }
                }
        );



    }
}