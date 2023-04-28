package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class random_test_view extends AppCompatActivity {

    Button random_next_btn;

    DBHelper dbh;

    TextView random_test_verb;
    EditText random_test_french, random_test_preterit, random_test_past_participle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_test_view);




        dbh = new DBHelper(this);
        ArrayList<Integer> selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");


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