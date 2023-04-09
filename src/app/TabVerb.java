import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TabVerb {
    private ArrayList<Verbs> arr;

    public TabVerb(int size){
        arr = new ArrayList<Verbs>();
    }

    public TabVerb(String filename){ // "/home/antoine/Prog/android/teachme.io/src/app/src/verbs.txt"
        arr = new ArrayList<Verbs>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line1, line2, line3, line4;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null && (line4 = reader.readLine()) != null) {
                arr.add(new Verbs(line4, line1, line2, line3));
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
}

