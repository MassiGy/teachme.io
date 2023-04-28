package com.example.teachmeio;

import static java.lang.System.exit;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;
import android.graphics.YuvImage;
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
import java.util.ArrayList;
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

    public ArrayList<Integer> selected_verbs_ids= new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);



        dbh = new DBHelper(this);

        // load verbs to db, the selection and fails count are suppose to be not known at this stage.
        // they will be updated if any selection has been found in the db.
        load_all_verbs_to_db(null, null);

        //  change each switch color + set its text + set if they are checked or not
        updateSwitchs();
        // Store boolean values when the user checks or unchecks a switch :
        add_boolean_listener_on_every_switch();


        submit_btn = findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // code to be executed when button is clicked
            public void onClick(View v) {




                Intent payload = new Intent(available_verbs_view.this, available_tests_view.class);

                payload.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);

                startActivity(payload);
            }
        });
    }

    public void load_all_verbs_to_db(boolean[] selection_field_values, int[] fails_field_values){

        TabVerb tv = new TabVerb();

        // set our iterators (indecies) to a default but wrong value.
        int selection_field_values_iterator = -1;
        int fails_field_values_iterator = -1;

        // check if the selection fields are known
        if(selection_field_values != null)
        {
            selection_field_values_iterator = 0;
        }
        // check if the fails field are known
        if(fails_field_values != null){
            fails_field_values_iterator = 0;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("verbs.txt")));
            String line1, line2, line3, line4;

            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {

                tv.arr.add(
                        new Verbs(
                                line4,
                                line1,
                                line2,
                                line3,
                                // if the fails count is known, set it as such, otherwise set it to 0
                                fails_field_values != null ? fails_field_values[fails_field_values_iterator] : 0,
                                // if the selection field is known, set it as such, otherwise set it to false ( non selected)
                                selection_field_values != null ? selection_field_values[selection_field_values_iterator] : false
                        ));

                // increment the iterators if used.
                if(selection_field_values != null)
                {
                    selection_field_values_iterator++;
                }
                if(fails_field_values != null){
                    fails_field_values_iterator++;
                }

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

        for(int i = 0 ; i < NB_VERBS ; ++i){
            dbh.insertVerb(tv.arr.get(i));
        }
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


    public void reset_fails(View v){
        // store the selected :
        boolean[] selected = new boolean[NB_VERBS];
        for(int i = 0 ; i < NB_VERBS ; ++i)
            selected[i] = dbh.getVerb(i+1).selected;
        // drop database if exists :
        dbh.deleteDatabase(this);

        // load verbs respecting the selection field values.
        load_all_verbs_to_db(selected, null);

        updateSwitchs();
    }

    public void reset_selection(View v){
        // store the nb_fails :
        int[] nb_fails = new int[NB_VERBS];
        for(int i = 0 ; i < NB_VERBS ; ++i)
            nb_fails[i] = dbh.getVerb(i+1).nb_fails;
        // drop database if exists :
        dbh.deleteDatabase(this);

        // load verbs respecting the fails values.
        load_all_verbs_to_db(null, nb_fails);

        updateSwitchs();
    }


    public void updateSwitchs(){
        Switch switchView;
        for(int i = 0 ; i < NB_VERBS ; ++i){
            switchView = findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Verbs actual = dbh.getVerb(i+1); // indexes of id in db starts from 1
            if(actual == null){
                System.out.println("error on verb i = " + String.valueOf(i+1));
                // skip current loop iteration
                continue;
            }
            String french = actual.french;
            String eng = actual.english;
            String pre = actual.preterit;
            String pp = actual.past_p;
            String fails = actual.nb_fails + "";
            switchView.setChecked(actual.selected);

            // if the verb is selected, then add its id to the list
            if(actual.selected == true ){
                selected_verbs_ids.add(i);
            }

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


