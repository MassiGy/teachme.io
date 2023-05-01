package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class random_test_view extends AppCompatActivity {
    // set a database connection helper object.
    DBHelper dbh;

    // instantiate our ui component objects.
    Button random_next_btn;
    EditText random_test_english, random_test_french, random_test_preterit, random_test_past_participle;

    // set a counter for the user score.
    public static int current_score;

    // set a reference to the selected_verbs_id list.
    ArrayList<Integer> selected_verbs_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_test_view);

        // map our ui components to our objects
        random_test_english = findViewById(R.id.random_test_english);
        random_test_french = findViewById(R.id.random_test_french);
        random_test_preterit = findViewById(R.id.random_test_preterit);
        random_test_past_participle = findViewById(R.id.random_test_past_participle);

        // set the database connection
        dbh = new DBHelper(this);

        // grab the selected_verbs_ids list from the passed intent.
        selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
        // grab the current_user_score list from the passed intent.
        current_score = getIntent().getExtras().getInt("current_score");


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




        // add a onClickListener that will trigger the user input validation.
        random_next_btn = findViewById(R.id.random_next_btn);
        random_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the user data and compare them to the verb.
                // if the input and the saved tenses does not match, increment the fails on the list.
                if(!current_verb.match(
                        random_test_french.getText().toString(),
                        random_test_english.getText().toString(),
                        random_test_preterit.getText().toString(),
                        random_test_past_participle.getText().toString())
                )
                {
                    // increment fails of the current verb on a seperated thread
                    Thread increment_fails_differed_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbh.increment_fails(selected_verbs_ids.get(0) + 1);
                        }
                    });
                    increment_fails_differed_thread.start();

                    // update switch text stored on all_switches_list that was constructed on home page.
                    // TODO: put this switch text manipulation to another thread since it is not a UI workload (perforamnce)

                    // get the old text
                    String oldText = available_verbs_view.all_switches[selected_verbs_ids.get(0)].getText().toString();
                    // get the list of all word from the old text
                    String[] old_text_words = oldText.split(" ");
                    // get the last word which is formatted as /fails_count\n/
                    String last_word = old_text_words[old_text_words.length-1];
                    // get the fails_count, remove the \n first.
                    int fails_count = Integer.valueOf(last_word.substring(0,last_word.length()-1));
                    // increment the fails count.
                    fails_count++;

                    // create a string builder to update the switch text
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    // newText = oldText with an incremented fails count.
                    String newText = oldText.substring(0, oldText.lastIndexOf(" ")) + " "+ fails_count + "\n";
                    SpannableString coloredPart = new SpannableString(newText);
                    coloredPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, newText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(coloredPart);
                    // set the text to the corresponding switch
                    available_verbs_view.all_switches[selected_verbs_ids.get(0)].setText(builder);

                    // display a failure message.
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "fail", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    // increment the score
                    current_score++;

                    // display a success message
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "success", Toast.LENGTH_SHORT);
                    toast.show();
                }

                // shift the list to remove the head
                selected_verbs_ids.remove(0);

                Intent exports;
                if(selected_verbs_ids.size() > 0){
                    // set next view to the same test view
                    exports = new Intent(random_test_view.this, classic_test_view.class);
                }
                else{
                    // otherwise, set next view to the available test view
                    exports = new Intent(random_test_view.this, available_tests_view.class);
                }

                // export the list to the next view
                exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                // export the score to the next view
                exports.putExtra("current_score", current_score);
                startActivity(exports);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // if back is pressed within an ongoing test, go back to the available test view.
        Intent exports = new Intent(random_test_view.this, available_tests_view.class);
        exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
        exports.putExtra("current_score", current_score);
        startActivity(exports);
    }
}