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

/*
    TabVerb is a helper class that will manager verbs_vectors.
 */
public class TabVerb {
    public ArrayList<Verbs> arr;

    public static int nb_verbs;
    public TabVerb(){
        arr = new ArrayList<Verbs>();
    }


    public Verbs getRandomVerbFromSelected(){
        // make sure that verbs_vector is not empty.
        if(arr.size() == 0) return null;

        // get a random value
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());

        // grab the random verb
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
}

