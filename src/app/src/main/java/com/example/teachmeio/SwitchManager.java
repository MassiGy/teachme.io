package com.example.teachmeio;

import static com.example.teachmeio.available_verbs_view.NB_VERBS;
import static com.example.teachmeio.available_verbs_view.all_switches;
import static com.example.teachmeio.available_verbs_view.all_switches_state;
import static com.example.teachmeio.available_verbs_view.all_switched_ready;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/*
    SwitchManager is a helper class that will take over
    the available_verbs_views.all_switches list construction within a differed thread.

    TODO: merge the load_verbs and the update_switches so as the loading and switch creation happens at once (performance)
 */
public class SwitchManager extends AppCompatActivity implements Runnable{

    // declare the database connection object.
    DBHelper dbh;

    // declare a context reference object
    Context context;


    SwitchManager(Context context, DBHelper dbh){
        // set the context to the caller's activity
        this.context = context;
        // use the same connection object then the caller activity
        this.dbh = dbh;
    }


    @Override
    public void run() {
        // set the routines that will be executed in a differed thread.

        // load the verbs from file to db ( on app startup )
        load_all_verbs_to_db(null, null);
        // construct the available_verbs_view.all_switches list.
        updateSwitchs();
    }

    public void load_all_verbs_to_db(boolean[] selection_field_values, int[] fails_field_values){

        // create a verbs_vector_manager.
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

        // declare a buffer object to read the file.
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(this.context.getResources().getAssets().open("verbs.txt")));
            String line1, line2, line3, line4;

            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {

                // add the verb to the verbs_vector_manager.
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
        // NOTE: do not make this in a differed thread since the updateSwitch()
        // need to query this information in the database.
        Verbs[] array = new Verbs[NB_VERBS];
        dbh.insertVerbs(tv.arr.toArray(array));

    }

    public void updateSwitchs(){

        // construct the available_verbs_view.all_switches list.
        for(int i = 0 ; i < NB_VERBS ; ++i){

            all_switches[i] = new Switch(context);
            all_switches[i].setId(i);

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
            System.out.println("´@@@@@@@@@@@@@@@" + i + " :: " + actual);

            // construct the available_verbs_view.all_switches_state list.
            all_switches_state[i] = actual.selected;


            // if the verb is selected, then add its id to the available_verbs_view.selected_verbs_ids list.
            if(actual.selected == true ){
                available_verbs_view.selected_verbs_ids.add(i);
            }

            String text ="\nfr: "+ french +"\n" +
                    "en: " + eng + "\n" +
                    "preterit: " + pre + "\n"+
                    "past.p: " + pp + "\n"
                    + "\nfails count: " +fails+"\n";

            SpannableString coloredPart = new SpannableString(text);

            coloredPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(coloredPart);


            all_switches[i].setText(builder);
        }

        System.out.println("switch texts are all set");

        // mark the end of the switches preps.
        all_switched_ready = true;

    }

}
