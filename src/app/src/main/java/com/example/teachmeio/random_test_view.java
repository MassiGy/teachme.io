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

        // get the first in the list verb from the database.

        Verbs current_verb = dbh.getVerb(selected_verbs_ids.get(0) + 1);

        // set the hint to a random tense.
        Random rand = new Random();
        int roll = rand.nextInt(5);

        System.out.println("~~~~~~~~~~~~~~~~~~");
        System.out.println(roll);
        System.out.println("~~~~~~~~~~~~~~~~~~");

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
                boolean current_test_passed =
                        current_verb.french.contains(random_test_french.getText().toString()) &&
                        current_verb.preterit.contains(random_test_preterit.getText().toString())&&
                        current_verb.past_p.contains(random_test_past_participle.getText().toString()) &&
                        current_verb.english.contains(random_test_english.getText().toString());



                // if test_passed continue to the next one,


                // otherwise, increment the fails on the list.




                // shift the list to remove the head
                selected_verbs_ids.remove(0);

                Intent exports;
                if(selected_verbs_ids.size() > 0){
                    // export the rest of the list to the same view
                    exports = new Intent(random_test_view.this, classic_test_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                }
                else{
                    exports = new Intent(random_test_view.this, available_tests_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                }

                startActivity(exports);

            }
        });
    }
}