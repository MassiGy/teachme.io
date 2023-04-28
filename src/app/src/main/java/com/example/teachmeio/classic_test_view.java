package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class classic_test_view extends AppCompatActivity {

    Button classic_next_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_test_view);

        classic_next_btn = findViewById(R.id.classic_next_btn);


         ArrayList<Integer> selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
         System.out.println(selected_verbs_ids);

        classic_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked

                // get the user data;


                // get the corresponding verb in the database.


                // shift the list to remove the head


                // export the rest of the list
                Intent exports = new Intent(classic_test_view.this, classic_test_view.class);
                exports.putIntegerArrayListExtra("selected_vers_ids", selected_verbs_ids);

                startActivity(exports);
            }
        });
    }
}