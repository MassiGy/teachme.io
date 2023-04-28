package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class classic_test_view extends AppCompatActivity {

    Button classic_next_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_test_view);

        classic_next_btn = findViewById(R.id.classic_next_btn);

        classic_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(classic_test_view.this, classic_test_view.class));
            }
        });
    }
}