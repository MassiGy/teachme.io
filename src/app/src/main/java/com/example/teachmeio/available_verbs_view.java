package com.example.teachmeio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class available_verbs_view extends AppCompatActivity {

    RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rv = findViewById(R.id.select_verbs);
        TabVerb tv = new TabVerb("./verbs.txt");
        List<String> verbs_list = tv.getList();
        rv.setT
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);
    }
}