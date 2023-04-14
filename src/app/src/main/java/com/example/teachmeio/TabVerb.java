package com.example.teachmeio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TabVerb {
    public ArrayList<Verbs> arr;

    public static int nb_verbs;
    public TabVerb(int size){
        arr = new ArrayList<Verbs>();
    }

    public TabVerb(String filename){
        arr = new ArrayList<Verbs>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line1, line2, line3, line4;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                arr.add(new Verbs(line4, line1, line2, line3));
                nb_verbs++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
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
}

