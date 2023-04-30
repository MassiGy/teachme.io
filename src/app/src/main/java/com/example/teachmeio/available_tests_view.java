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
    Button to_classic_test_btn, to_random_test_btn, to_home_btn;
    TextView score_field;
    @Override
    public void onBackPressed() {
        Intent exports;
        exports = new Intent(available_tests_view.this, available_verbs_view.class);
        startActivity(exports);
    }
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
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String txt = "Score : " + String.valueOf(current_score) + " pts";
        SpannableString coloredPart = new SpannableString(txt);
        coloredPart.setSpan(new ForegroundColorSpan(Color.BLUE), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(coloredPart);

        score_field.setText(builder);


        to_classic_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                if(payload.size() == 0){
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "All verbs selected have been tested click on HOME", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    Intent exports = new Intent(available_tests_view.this, classic_test_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", payload);
                    startActivity(exports);
                }
            }
        });
        to_random_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                if(payload.size() == 0){
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "All verbs selected have been tested click on HOME", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    Intent exports = new Intent(available_tests_view.this, random_test_view.class);
                    exports.putIntegerArrayListExtra("selected_verbs_ids", payload);
                    startActivity(exports);
                }
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