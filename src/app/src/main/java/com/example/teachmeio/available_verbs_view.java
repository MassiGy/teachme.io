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
import android.view.View;
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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class available_verbs_view extends AppCompatActivity {
    Button submit_btn;

    public static int NB_VERBS = 1104/4;
    DBHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        submit_btn = findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // code to be executed when button is clicked
                startActivity(new Intent(available_verbs_view.this, available_tests_view.class));
            }
        });

        dbh = new DBHelper(this);


        // ============= change each switch color + set its text + set if they are checked or not
        updateSwitchs();





        // ============== Store boolean values when the user checks or unchecks a switch :
        add_boolean_listener_on_every_switch();
    }

    public void add_boolean_listener_on_every_switch(){
        Switch[] switch_arr = new Switch[NB_VERBS];
        for(int i = 0 ; i < switch_arr.length ; ++i)
            switch_arr[i] = (Switch)findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));

        // Loop through the array of switch IDs and add an OnCheckedChangeListener to each switch
        for (int i = 0; i < switch_arr.length; i++) {
            final int FINAL_I = i;
            switch_arr[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Update the switch state in the array
                    dbh.updateSelected(FINAL_I+1, isChecked); // use of a final varaible is mandatory here // +1 because sql indexes are from 1
                    // System.out.println("changing selection of i = " + FINAL_I + " now it's : " + tv.arr.get(FINAL_I).selected); // Working
                }
            });
        }
    }

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // close the db
        dbh.close();
    }
   */

    public void reset_fails(View v){
        // store the selected :
        boolean[] selectedd = new boolean[NB_VERBS];
        for(int i = 0 ; i < NB_VERBS ; ++i)
            selectedd[i] = dbh.getVerb(i+1).selected;
        // drop database if exists :
        dbh.deleteDatabase(this);
        TabVerb tv = new TabVerb();
        //System.out.println("\n\n\n\n####### before calling the context this : assets : " + getResources().getAssets() + "\n\n\n\n");
        // ======================================================================================================================== Maybe load the verbs from a button that would reset the db
        // ============== file reading and loading into memory (TabVerb class)
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("verbs.txt")));
            String line1, line2, line3, line4;
            int it = 0;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                tv.arr.add(new Verbs(line4, line1, line2, line3, 0, selectedd[it]));
                it++;
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
        System.out.println("Verbs loaded");
        // ============= insert verbs into db
        for(int i = 0 ; i < NB_VERBS ; ++i){
            dbh.insertVerb(tv.arr.get(i));
        }
        System.out.println("Verbs insered into db");
        updateSwitchs();
    }

    public void reset_selection(View v){
        // store the nb_fails :
        int[] nb_fails = new int[NB_VERBS];
        for(int i = 0 ; i < NB_VERBS ; ++i)
            nb_fails[i] = dbh.getVerb(i+1).nb_fails;
        // drop database if exists :
         dbh.deleteDatabase(this);
        TabVerb tv = new TabVerb();
        //System.out.println("\n\n\n\n####### before calling the context this : assets : " + getResources().getAssets() + "\n\n\n\n");
        // ======================================================================================================================== Maybe load the verbs from a button that would reset the db
        // ============== file reading and loading into memory (TabVerb class)
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("verbs.txt")));
            String line1, line2, line3, line4;
            int it = 0;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                tv.arr.add(new Verbs(line4, line1, line2, line3, nb_fails[it], true));
                it++;
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
        System.out.println("Verbs loaded");
        // ============= insert verbs into db
        for(int i = 0 ; i < NB_VERBS ; ++i){
            dbh.insertVerb(tv.arr.get(i));
        }
        System.out.println("Verbs insered into db");
        updateSwitchs();
    }


    public void updateSwitchs(){
        Switch switchView;
        for(int i = 0 ; i < NB_VERBS ; ++i){
            switchView = findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Verbs actual = dbh.getVerb(i+1); // indexes of id in db starts from 1
            if(actual == null){
                System.out.println("error on verb i = " + i+1);
                // exit loop
                break;
            }
            String french = actual.french;
            String eng = actual.english;
            String pre = actual.preterit;
            String pp = actual.past_p;
            String fails = actual.nb_fails + "";
            switchView.setChecked(actual.selected);

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

            coloredPart = new SpannableString(fails);
            coloredPart.setSpan(new ForegroundColorSpan(Color.GREEN), 0, fails.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart).append(" ");

            switchView.setText(builder);
            // ============= set the text of the switch
        }
        System.out.println("switch texts are all set");
    }



}


