package ch.epfl.sdp.healthplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class WelcomeScreenActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        ImageView logo = (ImageView) findViewById(R.id.logo_image);
        logo.setImageResource(R.drawable.logo);
        Intent intent = new Intent(this, MainActivity.class);
        //Wait 3sec before launch HomeActivity
        handler.postDelayed(new Runnable() {
            public void run() {
                launchMainActivity(intent);
            }
        }, 3000);
        for(int i = 0; i<3; i++){
            pulse(i*1000, logo);
        }
        //If the users touch the screen before 3 sec, then launch HomeActivity
        View view = (View) findViewById(R.id.my_view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                launchMainActivity(intent);
                return true;
            }
        });
    }

    private void launchMainActivity(Intent intent){
        if(isFinishing())
            return;
        startActivity(intent);
        finish();
    }
    //pulse : each 5 msec alpha decrease of 1% until 0.5sec, then each 5msec alpha increase of 1%
    private void pulse(int delay, ImageView logo){
        for(int i = 0; i<=1000; i+=5) {
            float opacity;
            if(i<500){
                opacity = 1 - (int)(i/5)*0.01f;
            }else
                opacity = (int)(i-500)*0.01f;
            handler.postDelayed(new Runnable() {
                public void run() {
                    logo.setAlpha(opacity);
                }
            }, i + delay);
        }
    }
}