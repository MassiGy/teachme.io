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
import androidx.appcompat.app.AppCompatDelegate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class available_verbs_view extends AppCompatActivity {
    private Button submit_btn;

    // access the linear_layout_in_scroll_view to dynamically populate it.
    public static LinearLayout linearLayout_inScrollView;
    public static int NB_VERBS = 1108/4; // each verbs needs 4 lines and the verbs.txt file contains 1108 lines... this is a definitive count
    DBHelper dbh = new DBHelper(this);
    // set a boolean flag to mark the construction end of the switches list.
    public static boolean all_switched_ready = false;
    // declare the switches list that will be used to dynamically populate the view.
    public static Switch[] all_switches = new Switch[NB_VERBS];
    // declare a list of booleans that represent the switches states(checked or not)
    public static boolean[] all_switches_state = new boolean[NB_VERBS];
    // declare a list of verbs id that will hold the selected verbs id to be tested on.
    public static  ArrayList<Integer> selected_verbs_ids= new ArrayList<>();

    public static final int CLICK_SOUND_POSITIVE = 0;
    public static final int CLICK_SOUND_NEGATIVE = 1;
    private SoundManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // lock the bright mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); 

        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        // launch the loading dialog.
        final  LoadingDialog loadingDialog = new LoadingDialog(available_verbs_view.this, null);
        loadingDialog.startLoadingDialog();

        // create sound manager class
        sm = new SoundManager();
        sm.initSounds(this);
        // load sounds
        sm.addSound(CLICK_SOUND_POSITIVE, R.raw.win_video_game);
        sm.addSound(CLICK_SOUND_NEGATIVE, R.raw.bad_click);


        // connect the linear_layout_in_scroll_view
        linearLayout_inScrollView = findViewById(R.id.linear_layout_in_scroll_view);

        // connect the submit btn.
        submit_btn = findViewById(R.id.submit_btn);

        // add an onclick event listener
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // code to be executed when button is clicked
            public void onClick(View v) {
                if(dbh.getSelectedCount() == 0){ // block the user on this screen if there are no verbs selected
                    sm.playSound(CLICK_SOUND_NEGATIVE); // play fail sound
                    Context context = getApplicationContext();
                    Toast.makeText(context, "select at least one verb", Toast.LENGTH_SHORT).show(); // ask the user for a selection
                }else { // go on to the test selection screen
                    sm.playSound(CLICK_SOUND_POSITIVE); // play success sound
                    Context context = getApplicationContext();
                    Toast.makeText(context,dbh.getSelectedCount() + " verb(s) selected" , Toast.LENGTH_SHORT).show(); // explains the user how much verbs are selected
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
                                for (int i = 0; i < NB_VERBS - 1; i++) {

                                    // make sure that the switch is already constructed, otherwise go to next one
                                    if(all_switches[i] == null) continue;

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

                                            // System.out.println("on change triggered");

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
                    // wait for 5~6s to load and query all the verbs to and from the db.
                    5000
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
        Thread reset_fails_differed_thread= new Thread(new Runnable() {
            @Override
            public void run() {
                dbh.reset_fails();
            }
        });
        reset_fails_differed_thread.start();


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
