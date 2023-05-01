package com.example.teachmeio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class available_verbs_view extends AppCompatActivity {
    Button submit_btn;

    // access the linear_layout_in_scroll_view to dynamically populate it.
    public static LinearLayout linearLayout_inScrollView;
    public static int NB_VERBS = 1108/4;
    // create our database connection helper.
    DBHelper dbh = new DBHelper(this);
    // set a boolean flag to mark the construction end of the switches list.
    public static boolean all_switched_ready = false;
    // declare the switches list that will be used to dynamically populate the view.
    public static Switch[] all_switches = new Switch[NB_VERBS];
    // declare a list of booleans that represent the switches states(checked or not)
    public static boolean[] all_switches_state = new boolean[NB_VERBS];
    // declare a list of verbs id that will hold the selected verbs id to be tested on.
    public static  ArrayList<Integer> selected_verbs_ids= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        // launch the loading dialog.
        final  LoadingDialog loadingDialog = new LoadingDialog(available_verbs_view.this, null);
        loadingDialog.startLoadingDialog();

        // connect the linear_layout_in_scroll_view
        linearLayout_inScrollView = findViewById(R.id.linear_layout_in_scroll_view);

        // connect the submit btn.
        submit_btn = findViewById(R.id.submit_btn);

        // add an onclick event listener
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // code to be executed when button is clicked
            public void onClick(View v) {

                // if no verbs are selected, inform the user.
                // TODO: check the selected_verbs_ids list length instead (performance)
                if(dbh.getSelectedCount() == 0){
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "select at least one verb", Toast.LENGTH_SHORT);
                    toast.show();

                }else {
                    // otherwise, confirm to the user how many verbs are selected
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context,dbh.getSelectedCount() + " verb(s) selected" , Toast.LENGTH_SHORT);
                    toast.show();

                    // create the payload intent that will be exported to the next view
                    Intent payload = new Intent(available_verbs_view.this, available_tests_view.class);

                    // then append to it the selected_verbs_ids list.
                    payload.putIntegerArrayListExtra("selected_verbs_ids", selected_verbs_ids);

                    // remove every switch from the linear_layout since it will be repopulated from the list next time.
                    // otherwise we will get a "child already has a parent" error.
                    linearLayout_inScrollView.removeAllViews();

                    // launch second activity.
                    startActivity(payload);
                }
            }
        });

        if(!all_switched_ready){
            // if the switches are not ready, which means that the app just launched, then load them and
            // create the switches list in a seperated thread for performance gain.
            SwitchManager switchManager = new SwitchManager(available_verbs_view.this, dbh);
            Thread switchManagerThread = new Thread(switchManager);

            // launch the thread in the background
            switchManagerThread.start();

            // create a handler to delay a routine afterwards
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                            public void run() {
                                // stop the loading dialog box
                                loadingDialog.dismissDialog();

                                // populate the linear_layout_in_scroll_view
                                for (int i = 0; i < NB_VERBS; i++) {

                                    linearLayout_inScrollView.addView(all_switches[i]);

                                    // set the checked on the view before adding the event_listener
                                    // otherwise a non necessary database query will be executed
                                    all_switches[i].setChecked(all_switches_state[i]);

                                    // add the on change event listener to the switch.
                                    final int FINAL_I = i;
                                    all_switches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            // Update the switch state in the array
                                            dbh.updateSelected(FINAL_I+1, isChecked);

                                            System.out.println("on change triggered");

                                            if(isChecked == true) {
                                                available_verbs_view.selected_verbs_ids.add(FINAL_I);
                                            }else{
                                                available_verbs_view.selected_verbs_ids.remove(available_verbs_view.selected_verbs_ids.indexOf(FINAL_I));
                                            }
                                        }
                                    });
                                }
                            }
                        },
                    // wait for 6~7s to load and query all the verbs to and from the db.
                    7000
            );
        }
        else {
            // otherwise, if the switches are already constructed, just populate the linear_layout.

            // dismiss the loading dialog box
            loadingDialog.dismissDialog();

            for (int i = 0; i < NB_VERBS; i++) {
                linearLayout_inScrollView.addView(all_switches[i]);
            }
        }
    }

    // if the back button is pressed nothing should happen since we are at the home page
    public void onBackPressed() {}

    public void reset_fails(View v){

        // reset the fails on the database.
        // TODO: can be put on a seperated thread, even if it is not a frequent action (performance)
        dbh.reset_fails();

        // loop through the rendered switches and change the text so as it shows 0fails.
        Switch switchView;
        for(int i = 0 ; i < NB_VERBS ; ++i){
            switchView = all_switches[i];

            // get the old text of every switch
            String oldText = switchView.getText().toString();
            SpannableStringBuilder builder = new SpannableStringBuilder();

            if(!oldText.endsWith("0\n")){
                // newText = oldText with a zero instead of the number at the end.
                String newText = oldText.substring(0, oldText.lastIndexOf(" ")) + " 0\n";
                SpannableString coloredPart = new SpannableString(newText);
                coloredPart.setSpan(new ForegroundColorSpan(Color.BLACK), 0, newText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(coloredPart);

                // set the new text as the switch text.
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
        for(int i = 0 ; i < NB_VERBS ; ++i){
            all_switches[i].setChecked(false);
        }
    }
}
