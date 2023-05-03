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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class classic_test_view extends AppCompatActivity {


    // set a current_score counter
    public static int current_score;


    // declare our ui components objects
    Button classic_next_btn;
    EditText classic_test_english, classic_test_french, classic_test_preterit, classic_test_past_participle;

    // declare our database connection helper.
    DBHelper dbh;

    // set a selected_verbs_ids list reference.
    ArrayList<Integer> selected_verbs_ids;

    private SoundManager sm;
    private static final int CORRECT_SOUND = 0;
    private static final int WRONG_SOUND = 1;
    private static final int BACK_SOUND = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_test_view);

        // create sound manager and add sounds
        sm = new SoundManager();
        sm.initSounds(this);
        sm.addSound(CORRECT_SOUND, R.raw.correct_ans);
        sm.addSound(WRONG_SOUND, R.raw.bad_click);
        sm.addSound(BACK_SOUND, R.raw.pop);

        // map our ui components to our objects
        classic_next_btn = findViewById(R.id.classic_next_btn);
        classic_test_english = findViewById(R.id.classic_test_english);
        classic_test_french = findViewById(R.id.classic_test_french);
        classic_test_preterit = findViewById(R.id.classic_test_preterit);
        classic_test_past_participle = findViewById(R.id.classic_test_past_participle);

         // instantiate a database connection.
         dbh = new DBHelper(this);

         // get the selected_verbs_ids list from the intent
         selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
         // get the user score from the intent.
         current_score = getIntent().getExtras().getInt("current_score");



         // get the first in the list verb from the database.
         Verbs current_verb = dbh.getVerb(selected_verbs_ids.get(0)+1);

         // set the hint to the english tense.
         classic_test_english.setText(current_verb.getEnglish());

        // set a onclick event listener to the next btn that will trigger the user input validation.
        classic_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if the input and the saved tenses does not match, increment the fails on the list.
                if(!current_verb.match(
                        classic_test_french.getText().toString(),
                        current_verb.english,
                        classic_test_preterit.getText().toString(),
                        classic_test_past_participle.getText().toString())
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


                    // update the switch text on a seperated thread
                    Thread update_switch_text_differed_thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // update switch text stored on all_switches_list that was constructed on home page.

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
                        }
                    });
                    update_switch_text_differed_thread.start();

                    // inform the user about his failure by displaying a long toast with the correct information about the verb + sound
                    sm.playSound(WRONG_SOUND);
                    Context context = getApplicationContext();
                    Toast.makeText(context, "fail : \t"   + current_verb.getEnglish() + " \t"
                                                                    + current_verb.getFrench() + " \t"
                                                                    + current_verb.getPreterit() + " \t"
                                                                    + current_verb.getPast_p(), Toast.LENGTH_LONG).show();
                }else{
                    // increment the user score.
                    sm.playSound(CORRECT_SOUND);
                    current_score++;
                    // display success toast
                    Context context = getApplicationContext();
                    Toast.makeText(context, "pass", Toast.LENGTH_SHORT).show();
                }

                // shift the list to remove the head ( the current test verb )
                selected_verbs_ids.remove(0);


                Intent exports;
                if(selected_verbs_ids.size() > 0){
                    // set next view to the same view ( to be tested on )
                    exports = new Intent(classic_test_view.this, classic_test_view.class);
                } else{
                    // otherwise, set next view to the available test view.
                    exports = new Intent(classic_test_view.this, available_tests_view.class);
                }

                // export the rest of the list, and the score.
                exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                exports.putExtra("current_score", current_score);
                startActivity(exports);
            }
        });
    }

    public void onBackPressed() {
        sm.playSound(BACK_SOUND);
        // if back pressed while an ongoing test, go back to the available test view.
        Intent exports = new Intent(classic_test_view.this, available_tests_view.class);
        exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
        exports.putExtra("current_score", current_score);
        startActivity(exports);
    }
}