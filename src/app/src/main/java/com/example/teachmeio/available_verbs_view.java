package com.example.teachmeio;

import static java.lang.System.exit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        System.out.println("####### before calling the context this\n");

        File file_verbs = new File(this.getFilesDir(), "verbs.txt");
        System.out.println("####### after calling the context this\n######### " + file_verbs);

        FileInputStream fis = null;
        try {
            String file_to_read = "verbs.txt";
            System.out.println("about to open " + file_to_read);
            fis = new FileInputStream(file_to_read);
        }catch (java.io.FileNotFoundException e){
            System.out.println("impossible to open the file. here are the files inside the current directory : ");
            String[] files_of_current_directory = this.fileList();
            for(int i = 0 ; i < files_of_current_directory.length ; i++){
                System.out.println(files_of_current_directory[i]);
            }


            System.out.println("\n\n\n################################## FILE NOT FOUND " + fis);
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line1, line2, line3, line4;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                tv.arr.add(new Verbs(line4, line1, line2, line3));
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        }
        System.out.println("#########################################" + tv);
        for(int i = 0 ; i  < TabVerb.nb_verbs ; ++i){
            Switch s = (Switch)findViewById(R.id.switch0);
            s.setText("blabla");//tv.arr.get(i).toString());
            s.setChecked(tv.arr.get(i).selected);
        }
    }
}