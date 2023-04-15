package com.example.teachmeio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TabVerb {
    public ArrayList<Verbs> arr;

    public static int nb_verbs;
    public TabVerb(){
        arr = new ArrayList<Verbs>();
    }


    public Verbs getRandomVerbFromSelected(){
        if(arr.size() == 0) return null;
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        int res;
        while(!arr.get(res = r.nextInt(arr.size())).selected){}
        return arr.get(res);
    }
    public String toString(){
        String res = "";
        for(int  i = 0 ; i < arr.size() ; ++i)
            res += arr.get(i) + "\n";
        return res;
    }

    public List<String> getList(){
        return arr.stream()
                            .map(s -> s.toString())
                            .collect(Collectors.toList());
    }

    // SQLite table definition
    public static final String TABLE_NAME = "verbs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FRENCH = "french";
    public static final String COLUMN_ENGLISH = "english";
    public static final String COLUMN_PRETERIT = "preterit";
    public static final String COLUMN_PAST_P = "past_p";
    public static final String COLUMN_NUMBER_FAILS = "number_fails";
    public static final String COLUMN_SELECTED = "selected";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FRENCH + " TEXT,"
                    + COLUMN_ENGLISH + " TEXT,"
                    + COLUMN_PRETERIT + " TEXT,"
                    + COLUMN_PAST_P + " TEXT,"
                    + COLUMN_NUMBER_FAILS + " INTEGER,"
                    + COLUMN_SELECTED + " INTEGER"
                    + ")";


}

