package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splash_screen_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // create a new handler thread.
        Handler handler = new Handler();

        // add a delayed routine that will trigger the next view after 1s
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent payload = new Intent(splash_screen_view.this, available_verbs_view.class);
                startActivity(payload);

                // stop the splash screen activity, so as it can not be reached again.
                finish();
            }
        }, 1000);
    }
}