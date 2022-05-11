package ch.epfl.sdp.healthplay.kneetag;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import ch.epfl.sdp.healthplay.HomeScreenActivity;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;


public class FinishScreen extends AppCompatActivity {
    public static String WINNER = "ch.epfl.sdp.healthplay.kneetag.FinishScreen.WINNER";

    public FinishScreen() {
        // Required empty public constructor
    }
    Pair<Integer,Integer> pointComputation(int winner,int looser){
        winner=winner+(int)(30.0*(1.0-1.0/(1+ Math.pow(10, 1.0 * (looser - winner) / 400))));
        looser=looser+(int)(30.0*(-1.0/(1+ Math.pow(10, 1.0 * (winner - looser) / 400))));
        return new Pair<>(winner,Math.max(0,looser));
    }
    private void initButton(Button button,String userID){
        Database database=new Database();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database.getStats(userID,t->{
            if(!t.isSuccessful()){
                Log.e("ERROR", "EREREREROOORORO");
            }
            else{
                Map<String, Map<String, Number>> map = (Map<String, Map<String, Number>>) t.getResult().getValue();
                String hp;
                if (map != null && map.containsKey(Database.getTodayDate())) {
                    Map<String, Number> stats = map.get(Database.getTodayDate());
                    if (stats != null && stats.containsKey(Database.HEALTH_POINT)) {
                      hp=String.valueOf(stats.get(Database.HEALTH_POINT));


                    }
                    else {
                        hp = "0";
                    }
                }
                else{
                    hp="0";
                }

                String id=userID.equals("YOU")?mAuth.getCurrentUser().getUid():userID;
                database.readField(id,Database.USERNAME,task -> {
                    if(!task.isSuccessful()){
                        Log.e("ERROR", "EREREREROOORORO");
                    }
                    else {
                        String username=(String) task.getResult().getValue();
                        String myPts = hp + " pts";

                        StringBuilder sb = new StringBuilder();
                        sb.append(username);
                        while(sb.length() <= 40) {
                            sb.append(' ');
                        }

                        sb.append(myPts);
                        System.out.println(sb);
                        button.setText(sb.toString());
                    }
                });
            }
        });

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gameended);
        super.onCreate(savedInstanceState);
        String winner=getIntent().getStringExtra("WINNER_NAME");
        String winnerId=getIntent().getStringExtra("WINNER_ID");
        String looser=getIntent().getStringExtra("LOOSER_NAME");
        String looser_id=getIntent().getStringExtra("LOOSER_ID");
        System.out.println(winner+winnerId+looser+looser_id);
        Button winnerButton=findViewById(R.id.winner);
        Button looserButton=findViewById(R.id.looser);
        System.out.println(winner+winnerId+looser+looser_id);
        if(getIntent().getBooleanExtra("RANKED",false)) {

            initButton(winnerButton,winnerId);
            initButton(looserButton, looser_id);
        }
        else
        {
            winnerButton.setVisibility(View.INVISIBLE);
            looserButton.setVisibility(View.INVISIBLE);
        }

        MediaPlayer.create(this, R.raw.notification).start();

        //display the winner
        TextView text=findViewById(R.id.winner_display);
        text.setText(getString(R.string.winnerMessage)+winner);
        //rerun the game
        findViewById(R.id.restart).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FinishScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        //go back to the menu
        findViewById(R.id.quit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FinishScreen.this, HomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }
}


