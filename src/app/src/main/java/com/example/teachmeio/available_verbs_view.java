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
    public static LinearLayout linearLayout_inScrollView;
    public static int NB_VERBS = 1108/4;
    DBHelper dbh = new DBHelper(this);
    public static boolean all_switched_ready = false;
    public static Switch[] all_switches = new Switch[NB_VERBS];
    public static boolean[] all_switches_state = new boolean[NB_VERBS];
    public static  ArrayList<Integer> selected_verbs_ids= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        final  LoadingDialog loadingDialog = new LoadingDialog(available_verbs_view.this, null);
        loadingDialog.startLoadingDialog();

        linearLayout_inScrollView = findViewById(R.id.linear_layout_in_scroll_view);
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
                    linearLayout_inScrollView.removeAllViews();
                    startActivity(payload);
                }
            }
        });

        if(!all_switched_ready){
            SwitchManager switchManager = new SwitchManager(available_verbs_view.this, dbh);
            Thread switchManagerThread = new Thread(switchManager);

            switchManagerThread.start();
            System.out.println("after the thread lunch.");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                            public void run() {
                                loadingDialog.dismissDialog();
                                for (int i = 0; i < NB_VERBS; i++) {

                                    linearLayout_inScrollView.addView(all_switches[i]);
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
                    7000
            );
        }
        else {
            loadingDialog.dismissDialog();

            for (int i = 0; i < NB_VERBS; i++) {
                linearLayout_inScrollView.addView(all_switches[i]);
            }
        }
    }

    public void onBackPressed() {}

    public void reset_fails(View v){

        dbh.reset_fails();

        Switch switchView;
        for(int i = 0 ; i < NB_VERBS ; ++i){
            switchView = all_switches[i];

            String oldText = switchView.getText().toString();
            SpannableStringBuilder builder = new SpannableStringBuilder();

            if(!oldText.endsWith("0\n")){
                // newText = oldText with a zero instead of the number at the end.
                String newText = oldText.substring(0, oldText.lastIndexOf(" ")) + " 0\n";
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
        for(int i = 0 ; i < NB_VERBS ; ++i){
            all_switches[i].setChecked(false);
        }
    }
}
