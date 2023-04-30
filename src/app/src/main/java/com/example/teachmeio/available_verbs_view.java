package com.example.teachmeio;

import static java.lang.System.exit;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import android.widget.Toast;


public class available_verbs_view extends AppCompatActivity {
    Button submit_btn;
   

    public static int NB_VERBS = 1104/4;
    DBHelper dbh = new DBHelper(this);


    public ArrayList<Integer> selected_verbs_ids= new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);


        // load verbs to db, the selection and fails count are suppose to be not known at this stage.
        // they will be updated if any selection has been found in the db.
        load_all_verbs_to_db(null, null);

        //  change each switch color + set its text + set if they are checked or not
        updateSwitchs();


        submit_btn = findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // code to be executed when button is clicked
            public void onClick(View v) {
                if(dbh.getSelectedCount() == 0){
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "select at least one verb", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context,dbh.getSelectedCount() + " verb(s) selected" , Toast.LENGTH_SHORT);
                    toast.show();
                    Intent payload = new Intent(available_verbs_view.this, available_tests_view.class);

                    payload.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);

                    startActivity(payload);
                }
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
        // insert all verbs at once for better performance.
        Verbs[] array = new Verbs[NB_VERBS];
        dbh.insertVerbs(tv.arr.toArray(array));

    }

    public void reset_fails(View v){

        dbh.reset_fails();

        Switch switchView;
        for(int i = 0 ; i < NB_VERBS ; ++i){
            switchView = findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));

            String oldText = switchView.getText().toString();
            SpannableStringBuilder builder = new SpannableStringBuilder();

            if(!oldText.endsWith("0")){
                // newText = oldText with a zero instead of the number at the end.
                String newText = oldText.substring(0, oldText.lastIndexOf(" ")) + " 0 ";
                SpannableString coloredPart = new SpannableString(newText);
                coloredPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, newText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(coloredPart);

                switchView.setText(builder);
            }
        }
    }

    public void reset_selection(View v){
        /*
        To reset the selections, only change the switch state to false,
        then the switchOnChangeListener will be triggered to modify it
        in the database.

         */
        Switch switchView;
        for(int i = 0 ; i < NB_VERBS ; ++i){
            switchView = findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));
            switchView.setChecked(false);
        }

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

            String text ="fr: "+ french +"\n" +
                    "en: " + eng + "\n" +
                    "preterit: " + pre + "\n"+
                    "past.p: " + pp + "\n"
                    + "\nfails count: " +fails;

            SpannableString coloredPart = new SpannableString(text);

            coloredPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart);


            switchView.setText(builder);

            // add the on change event listener to the switch.
            final int FINAL_I = i;
            switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Update the switch state in the array
                    dbh.updateSelected(FINAL_I+1, isChecked);

                    System.out.println("on change triggered");

                    if(isChecked == true) {
                        selected_verbs_ids.add(FINAL_I);
                    }else{
                        selected_verbs_ids.remove(selected_verbs_ids.indexOf(FINAL_I));
                    }
                }
            });
        }
        System.out.println("switch texts are all set");
    }



}
