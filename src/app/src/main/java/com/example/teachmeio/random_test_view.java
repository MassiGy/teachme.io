package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class random_test_view extends AppCompatActivity {

    Button random_next_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_test_view);

        random_next_btn = findViewById(R.id.classic_next_btn);

        random_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(random_test_view.this, random_test_view.class));
            }
        });
    }
}