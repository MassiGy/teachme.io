package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class available_tests_view extends AppCompatActivity {
    Button to_classic_test_btn, to_random_test_btn, to_history_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_tests_view);

        to_classic_test_btn = findViewById(R.id.to_classic_test_btn);
        to_random_test_btn = findViewById(R.id.to_random_test_btn);
        to_history_btn = findViewById(R.id.to_history_btn);

        to_classic_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(available_tests_view.this, classic_test_view.class));
            }
        });
        to_random_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(available_tests_view.this, random_test_view.class));
            }
        });
        to_history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(available_tests_view.this, available_tests_view.class));
            }
        });
    }
}