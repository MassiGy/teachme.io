package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Arrays;
import java.util.List;

public class available_verbs_view extends AppCompatActivity {

     ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lv = findViewById(R.id.verbs_selection);
        TabVerb tv = new TabVerb("./verbs.txt");
        List<String> verbs_list = tv.getList();

        for(int i = 0  ; i < verbs_list.size() ; ++i){
            Switch s = new Switch(available_verbs_view.this);
            s.setText(verbs_list.get(i));
            lv.addView(s);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);
    }
}