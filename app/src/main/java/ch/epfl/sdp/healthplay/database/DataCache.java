package ch.epfl.sdp.healthplay.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DataCache {
    private Map<String, Map<String, Number>> current_map;
    private Map<String, String> mapProfile = new HashMap<>();
    private Context context;
    private Database db = new Database();
    private FirebaseAuth fa = FirebaseAuth.getInstance();
    private String cacheNameStats = "cacheFileStats.txt", cacheNameProfile = "cacheFileProfile.txt";
    private String UserID = "UserId", QrCode = "QrCode";

    public DataCache(Context context){
        init(context);
    }

    /*
     * init the listener to firebaseAuth
     * @param context for initilazation of cachedirectory
     */
    public void init(Context context){
        this.context = context;
        fa.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = fa.getCurrentUser();
                //if the user is not logged, read the cache file
                if(user==null)
                    read();
                else {
                    //if the user logged, store the database in the cache file
                    db.mDatabase.child(Database.USERS).child(user.getUid()).child(Database.STATS).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            storeStats(user.getUid());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    listenField(user.getUid(), Database.BIRTHDAY);
                    listenField(user.getUid(), Database.NAME);
                    listenField(user.getUid(), Database.SURNAME);
                    listenField(user.getUid(), Database.USERNAME);
                    listenField(user.getUid(), Database.IMAGE);
                    store(user.getUid());
                }
            }
        });
    }

    private void listenField(String userID, String field){
        db.mDatabase.child(Database.USERS).child(userID).child(field).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.getValue() != null) {
                        storeProfile(userID, field);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /**
     * readFile the file in format json
     * @return the text of the file
     */
    private String readFile(String cacheName) throws IOException{
        //Open File Input
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(new File(context.getCacheDir(),"")+cacheName)));
        //Create the Reader for the file
        InputStreamReader reader = new InputStreamReader(in);
        int character = 0;
        StringBuilder stringBuilder = new StringBuilder();
        //Read
        while((character = in.read()) != -1){
            stringBuilder.append((char)character);
        }
        //Log.d("Debug lecture du fichier : ", stringBuilder.toString());
        in.close();
        return stringBuilder.toString();
    }
    /**
     * read the file in format json, convert to a Map<String, Map<String, Number>>
     *
     */
    private void readStats(){
        try {
            String jsonInput = readFile(cacheNameStats);
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, HashMap<String, Number>>> typeRef = new TypeReference<HashMap<String, HashMap<String, Number>>>(){};
            Map<String, Map<String, Number>> map = mapper.readValue(jsonInput, typeRef);
            current_map = map;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readProfile(){
        try {
            String jsonInput = readFile(cacheNameProfile);
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>(){};
            Map<String, String> map = mapper.readValue(jsonInput, typeRef);
            mapProfile = map;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(){
        readProfile();
        readStats();
    }

    /**
     * store the database in the cache file in json format
     * @param id String user id
     */
    private void storeStats(String id){
        db.getStats(id, task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) task.getResult().getValue();
                current_map = map;
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String, HashMap<String, Number>>> typeRef = new TypeReference<HashMap<String, HashMap<String, Number>>>(){};
                ObjectOutputStream out = null;
                try {
                    String json = mapper.writerFor(typeRef).writeValueAsString(map);
                    //Log.d("Ecriture", json);
                    out = new ObjectOutputStream(new FileOutputStream(new File(context.getCacheDir(),"")+cacheNameStats));
                    out.writeBytes(json);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * @return the current map in the cache
     */

    private void storeProfile(String id, String field){
        db.readField(id, field, task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                mapProfile.put(field, (String) task.getResult().getValue());
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>(){};
                ObjectOutputStream out = null;
                try {
                    String json = mapper.writerFor(typeRef).writeValueAsString(mapProfile);
                    //Log.d("Ecriture", json);
                    out = new ObjectOutputStream(new FileOutputStream(new File(context.getCacheDir(),"")+cacheNameProfile));
                    out.writeBytes(json);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void store(String userId){
        storeStats(userId);
        mapProfile.put(UserID, userId);
        mapProfile.put(QrCode, getStringFromBitmap(QRCode.from(userId).bitmap()));
        storeProfile(userId, Database.NAME);
        storeProfile(userId, Database.USERNAME);
        storeProfile(userId, Database.SURNAME);
        storeProfile(userId, Database.BIRTHDAY);
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public Map<String, Map<String, Number>> getDataMap(){
        return current_map;
    }

    public Map<String, Map<String, String>> getDataMapCalendar(){
        if(current_map == null || current_map.isEmpty()) return null;
        Map<String , Map<String, String>> map = new HashMap<>();
        current_map.forEach((date, datas) -> {
            Map<String, String> inter = new HashMap<>();
            datas.forEach((l, d) -> {
                inter.put(l, String.valueOf(d));
            });
            map.put(date, inter);
        });
        return map;
    }

    public String getField(String field){
        return mapProfile.get(field);
    }

    public String getUserId(){
        return mapProfile.get(UserID);
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public Bitmap getQrCode(){
        return getBitmapFromString(mapProfile.get(QrCode));
    }
}
