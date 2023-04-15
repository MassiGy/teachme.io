package com.example.teachmeio;

import static java.lang.System.exit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class available_verbs_view extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_verbs_view);

        TabVerb tv = new TabVerb();
        System.out.println("\n\n\n\n####### before calling the context this : assets : " + getResources().getAssets() + "\n\n\n\n");


        // voici comment lire un fichier texte sous android. Le fichier doit être placé dans le répertoire Assets : android.content.res.AssetManager@fbe4f01
        BufferedReader reader = null;
        int nb_verb = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("verbs.txt")));
            String line1, line2, line3, line4;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                tv.arr.add(new Verbs(line4, line1, line2, line3));
                nb_verb++;
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

        Switch switchView;
        for(int i = 0 ; i < nb_verb ; ++i){
            switchView = findViewById(getResources().getIdentifier("switch" + i, "id", getPackageName()));
            switchView.setText(tv.arr.get(i).toString());
        }


    }



}