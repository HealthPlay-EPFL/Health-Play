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
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import ch.epfl.sdp.healthplay.HomeScreenActivity;

import ch.epfl.sdp.healthplay.R;
import ch.epfl.sdp.healthplay.database.Database;


public class FinishScreen extends AppCompatActivity {

    private Database database=new Database();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int POINT_WIN= 30;
    private int STRING_LENGTH= 30;
    private int POINT_LOOSE= 10;
    public FinishScreen() {
        // Required empty public constructor
    }

    //winner and looser point modification
    void pointComputation(String winner,String looser) {

            winner = winner.equals("YOU") ? mAuth.getCurrentUser().getUid() : winner;
            looser = looser.equals("YOU") ? mAuth.getCurrentUser().getUid() : looser;
            database.addHealthPoint(winner, POINT_WIN);
            database.addHealthPoint(looser, POINT_LOOSE);




    }
    //Initialize the point display button and the text displaying the winner.
    private void initButton(Button button,String userID,int points){


        database.getStats(userID.equals("YOU")?mAuth.getCurrentUser().getUid():userID,t->{
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
                        String username = (String) task.getResult().getValue();
                        String myPts = hp + " pts";

                        StringBuilder sb = new StringBuilder();
                        sb.append(username);
                        while (sb.length() <= STRING_LENGTH) {
                            sb.append(' ');
                        }
                        sb.append(myPts);
                        sb.append(" +" + points + " pts");

                        button.setText(sb.toString());
                        if (points == POINT_WIN) {
                            TextView text = findViewById(R.id.winner_display);
                            text.setText(getString(R.string.winnerMessage) + " " + username);
                        }
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
        //recover information about winner/looser from main activity.
        String winnerId=getIntent().getStringExtra("WINNER_ID");

        String looser_id=getIntent().getStringExtra("LOOSER_ID");

        if(!internetIsConnected()){
            TextView text = findViewById(R.id.winner_display);
            text.setText(getString(R.string.winnerMessage) + " " + winnerId);
        }

        Button winnerButton=findViewById(R.id.winner);
        Button looserButton=findViewById(R.id.looser);
        //change behavior if the game is ranked or unranked
        if(getIntent().getBooleanExtra("RANKED",false)) {

            initButton(winnerButton,winnerId,POINT_WIN);
            initButton(looserButton, looser_id,POINT_LOOSE);
            pointComputation(winnerId,looser_id);
        }
        else
        {
            winnerButton.setVisibility(View.INVISIBLE);
            looserButton.setVisibility(View.INVISIBLE);
        }

        MediaPlayer.create(this, R.raw.notification).start();
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
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

}


