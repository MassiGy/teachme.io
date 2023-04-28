package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class classic_test_view extends AppCompatActivity {

    Button classic_next_btn;

    EditText classic_test_english, classic_test_french, classic_test_preterit, classic_test_past_participle;

    DBHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_test_view);

        classic_next_btn = findViewById(R.id.classic_next_btn);
        classic_test_english = findViewById(R.id.classic_test_english);
        classic_test_french = findViewById(R.id.classic_test_french);
        classic_test_preterit = findViewById(R.id.classic_test_preterit);
        classic_test_past_participle = findViewById(R.id.classic_test_past_participle);


         dbh = new DBHelper(this);
         ArrayList<Integer> selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");

         System.out.println(selected_verbs_ids);
         // get the first in the list verb from the database.
         Verbs current_verb = dbh.getVerb(selected_verbs_ids.get(0)+1);


         // set the hint to the english tense.
         classic_test_english.setText(current_verb.english);





        classic_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked

                // otherwise, increment the fails on the list.
                if(!current_verb.match(classic_test_french.getText().toString(), current_verb.english, classic_test_preterit.getText().toString(), classic_test_past_participle.getText().toString()))
                    dbh.increment_fails(selected_verbs_ids.get(0));

                // shift the list to remove the head
                selected_verbs_ids.remove(0);

                Intent exports;
                if(selected_verbs_ids.size() > 0){
                    // export the rest of the list to the same view
                    exports = new Intent(classic_test_view.this, classic_test_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                }
                else{
                    exports = new Intent(classic_test_view.this, available_tests_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                }

                startActivity(exports);
            }
        });
    }
}