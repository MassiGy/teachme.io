package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class available_tests_view extends AppCompatActivity {
    Button to_classic_test_btn, to_random_test_btn, to_home_btn;
    TextView score_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_tests_view);

        ArrayList<Integer> payload = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
        int current_score = getIntent().getExtras().getInt("current_score");

        to_classic_test_btn = findViewById(R.id.to_classic_test_btn);
        to_random_test_btn = findViewById(R.id.to_random_test_btn);
        to_home_btn = findViewById(R.id.to_home_btn);
        score_field = findViewById(R.id.score_text_field);

        // if the list is empty go back to selection.
        score_field.setText("Score : " + String.valueOf(current_score) + "pts");


        to_classic_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                Intent exports = new Intent(available_tests_view.this, classic_test_view.class);
                exports.putIntegerArrayListExtra("selected_verbs_ids", payload);
                startActivity(exports);
            }
        });
        to_random_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                Intent exports = new Intent(available_tests_view.this, random_test_view.class);
                exports.putIntegerArrayListExtra("selected_verbs_ids", payload);
                startActivity(exports);
            }
        });
        to_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(available_tests_view.this, available_verbs_view.class));
            }
        });
    }
}