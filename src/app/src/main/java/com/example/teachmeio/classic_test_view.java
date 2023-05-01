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

    Button classic_next_btn;

    public static int current_score;

    EditText classic_test_english, classic_test_french, classic_test_preterit, classic_test_past_participle;

    DBHelper dbh;

    ArrayList<Integer> selected_verbs_ids;

    @Override
    public void onBackPressed() {
        Intent exports;
        exports = new Intent(classic_test_view.this, available_tests_view.class);
        exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
        exports.putExtra("current_score", current_score);
        startActivity(exports);
    }
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
         selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
         current_score = getIntent().getExtras().getInt("current_score");



         System.out.println("selected_verbs_ids = " + selected_verbs_ids);
         System.out.println("current_score = " + current_score);

         // get the first in the list verb from the database.
         Verbs current_verb = dbh.getVerb(selected_verbs_ids.get(0)+1);


         // set the hint to the english tense.
         classic_test_english.setText(current_verb.getEnglish());






        classic_next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked

                // if the input and the saved tenses does not match, increment the fails on the list.
                if(!current_verb.match(
                        classic_test_french.getText().toString(),
                        current_verb.english,
                        classic_test_preterit.getText().toString(),
                        classic_test_past_participle.getText().toString())
                ){
                    dbh.increment_fails(selected_verbs_ids.get(0) + 1);

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

                    // inform the user about his failure.
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "fail : \t"   + current_verb.getEnglish() + " \t"
                                                                    + current_verb.getFrench() + " \t"
                                                                    + current_verb.getPreterit() + " \t"
                                                                    + current_verb.getPast_p(), Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    current_score++;
                    // display success toast
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "pass", Toast.LENGTH_SHORT);
                    toast.show();
                }

                // shift the list to remove the head


                selected_verbs_ids.remove(0);



                Intent exports;

                if(selected_verbs_ids.size() > 0){
                    // export the rest of the list to the same view
                    exports = new Intent(classic_test_view.this, classic_test_view.class);
                }
                else{
                    exports = new Intent(classic_test_view.this, available_tests_view.class);
                }

                exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                exports.putExtra("current_score", current_score);
                startActivity(exports);
            }
        });
    }
}