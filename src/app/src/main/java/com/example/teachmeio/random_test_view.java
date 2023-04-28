package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Random;

public class random_test_view extends AppCompatActivity {

    Button random_next_btn;

    public static int current_score;

    DBHelper dbh;


    EditText random_test_english, random_test_french, random_test_preterit, random_test_past_participle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_test_view);


        random_test_english = findViewById(R.id.random_test_english);
        random_test_french = findViewById(R.id.random_test_french);
        random_test_preterit = findViewById(R.id.random_test_preterit);
        random_test_past_participle = findViewById(R.id.random_test_past_participle);


        dbh = new DBHelper(this);
        ArrayList<Integer> selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
        current_score = getIntent().getExtras().getInt("current_score");

        System.out.println("selected_verbs_ids = " + selected_verbs_ids);
        System.out.println("current_score = " + current_score);

        // get the first in the list verb from the database.

        Verbs current_verb = dbh.getVerb(selected_verbs_ids.get(0) + 1);

        // set the hint to a random tense.
        Random rand = new Random();

        int roll = rand.nextInt(5);

        if(roll ==1){
            // set the english tense as hint
            random_test_english.setText(current_verb.english);
        }
        else if (roll == 2){
            // set the french tense as a hint
            random_test_french.setText(current_verb.french);
        }
        else if (roll == 3){
            // set the pretirit tense as a hint
            random_test_preterit.setText(current_verb.preterit);
        }
        else {
            // set the past participle tense as hint
            random_test_past_participle.setText(current_verb.past_p);
        }





        random_next_btn = findViewById(R.id.random_next_btn);
        random_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked

                // get the user data and compare them to the verb.
                // if the input and the saved tenses does not match, increment the fails on the list.
                if(!current_verb.match(
                        random_test_french.getText().toString(),
                        random_test_english.getText().toString(),
                        random_test_preterit.getText().toString(),
                        random_test_past_participle.getText().toString())
                ){
                    dbh.increment_fails(selected_verbs_ids.get(0) + 1);
                }else{
                    current_score++;
                }

                // shift the list to remove the head
                selected_verbs_ids.remove(0);

                Intent exports;
                if(selected_verbs_ids.size() > 0){
                    // export the rest of the list to the same view
                    exports = new Intent(random_test_view.this, classic_test_view.class);
                }
                else{
                    exports = new Intent(random_test_view.this, available_tests_view.class);
                }
                exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                exports.putExtra("current_score", current_score);
                startActivity(exports);
            }
        });
    }
}