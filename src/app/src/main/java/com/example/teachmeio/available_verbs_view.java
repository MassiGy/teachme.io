package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Arrays;
import java.util.List;

public class available_verbs_view extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        TabVerb tv = new TabVerb("./verbs.txt");
        for(int i = 0 ; i  < TabVerb.nb_verbs ; ++i){
            Switch s = (Switch)findViewById(R.id.switch0);
            s.setText("blabla");//tv.arr.get(i).toString());
            s.setChecked(tv.arr.get(i).selected);
        }
    }
}