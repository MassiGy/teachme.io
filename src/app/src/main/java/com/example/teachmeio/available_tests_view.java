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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class available_tests_view extends AppCompatActivity {
    // instantiate the ui components objects
    private Button to_classic_test_btn, to_random_test_btn, to_home_btn;
    private TextView score_field;

    private SoundManager sm;
    public static final int TEST_SOUND = 0;
    public static final int BACK_HOME_SOUND = 1;
    public static final int CLICK_SOUND_NEGATIVE = 2;
    @Override
    public void onBackPressed() {
        sm.playSound(BACK_HOME_SOUND);
        Intent exports;
        exports = new Intent(available_tests_view.this, available_verbs_view.class); // go to the verb selection screen
        startActivity(exports);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_tests_view);

        // create sound manager and add sounds
        sm = new SoundManager();
        sm.initSounds(this);
        sm.addSound(TEST_SOUND, R.raw.winning_notification);
        sm.addSound(BACK_HOME_SOUND, R.raw.pop);
        sm.addSound(CLICK_SOUND_NEGATIVE, R.raw.bad_click);

        // collect the selected_verbs_ids list passed from either the home view or a test view.
        ArrayList<Integer> selected_verbs_ids = getIntent().getExtras().getIntegerArrayList("selected_verbs_ids");
        // collect the current score passed from the test views, 0 if none.
        int current_score = getIntent().getExtras().getInt("current_score");

        // map our ui components to our objects
        to_classic_test_btn = findViewById(R.id.to_classic_test_btn);
        to_random_test_btn = findViewById(R.id.to_random_test_btn);
        to_home_btn = findViewById(R.id.to_home_btn);
        score_field = findViewById(R.id.score_text_field);

        // set the score text view text.
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String txt = "Score : " + String.valueOf(current_score) + " pts";
        SpannableString coloredPart = new SpannableString(txt);
        coloredPart.setSpan(new ForegroundColorSpan(Color.BLUE), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(coloredPart);

        score_field.setText(builder);



        to_classic_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the selected_verbs_ids is empty, inform the user to go back home page.
                if(selected_verbs_ids.size() == 0){
                    sm.playSound(CLICK_SOUND_NEGATIVE);
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "All verbs selected have been tested click on HOME", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    // otherwise, transfer the user to the classic test view, passing the list.
                    sm.playSound(TEST_SOUND);
                    Intent exports = new Intent(available_tests_view.this, classic_test_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                    startActivity(exports);
                }
            }
        });
        to_random_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the selected_verbs_ids is empty, inform the user to go back home page.
                if(selected_verbs_ids.size() == 0){
                    sm.playSound(CLICK_SOUND_NEGATIVE);
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "All verbs selected have been tested click on HOME", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    sm.playSound(TEST_SOUND);
                    // otherwise, transfer the user to the random test view, passing the list.
                    Intent exports = new Intent(available_tests_view.this, random_test_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);
                    startActivity(exports);
                }
            }
        });
        to_home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // otherwise, transfer the user back to home view,
                sm.playSound(BACK_HOME_SOUND);
                startActivity(new Intent(available_tests_view.this, available_verbs_view.class));
            }
        });
    }
}