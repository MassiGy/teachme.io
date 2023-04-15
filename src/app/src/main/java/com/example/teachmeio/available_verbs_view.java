package com.example.teachmeio;

import static java.lang.System.exit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class available_verbs_view extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        TabVerb tv = new TabVerb();
        //System.out.println("\n\n\n\n####### before calling the context this : assets : " + getResources().getAssets() + "\n\n\n\n");

        // ============== file reading and loading into memory (TabVerb class)
        BufferedReader reader = null;
        int nb_verb = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("verbs.txt")));
            String line1, line2, line3, line4;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                tv.arr.add(new Verbs(line4, line1, line2, line3));
                nb_verb++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // ============= Once the file is read, we need to change each switch color

        Switch switchView;
        for(int i = 0 ; i < nb_verb ; ++i){
            switchView = findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String french = tv.arr.get(i).french;
            String eng = tv.arr.get(i).english;
            String pre = tv.arr.get(i).preterit;
            String pp = tv.arr.get(i).past_p;

            // ================ For each verb, change the color depending on the version
            SpannableString coloredPart = new SpannableString(french);
            coloredPart.setSpan(new ForegroundColorSpan(Color.GRAY), 0, french.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart).append(" ");

            coloredPart = new SpannableString(eng);
            coloredPart.setSpan(new ForegroundColorSpan(Color.BLUE), 0, eng.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart).append(" ");

            coloredPart = new SpannableString(pre);
            coloredPart.setSpan(new ForegroundColorSpan(Color.CYAN), 0, pre.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart).append(" ");

            coloredPart = new SpannableString(pp);
            coloredPart.setSpan(new ForegroundColorSpan(Color.RED), 0, pp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart).append(" ");

            switchView.setText(builder);
            // ============= set the text of the switch
        }




        // ============== Store boolean values when the user checks or unchecks a switch :
        Switch[] switch_arr = new Switch[nb_verb];
        for(int i = 0 ; i < switch_arr.length ; ++i)
            switch_arr[i] = (Switch)findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));

        // Array to store the state of each switch
        boolean[] switchStates = new boolean[nb_verb];

        // Loop through the array of switch IDs and add an OnCheckedChangeListener to each switch
        for (int i = 0; i < switch_arr.length; i++) {
            final int FINAL_I = i;
            switch_arr[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Update the switch state in the array
                    tv.arr.get(FINAL_I).selected = isChecked; // use of a final varaible is mandatory here
                    // System.out.println("changing selection of i = " + FINAL_I + " now it's : " + tv.arr.get(FINAL_I).selected); // Working
                }
            });
        }



    }



}