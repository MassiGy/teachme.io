package com.example.teachmeio;

public class Verbs {

    // set the verbs attributes
    public String french;
    public String english;
    public String preterit;
    public String past_p;

    public boolean selected;

    public int nb_fails;
    public int id;

    public Verbs(){
        french = "";
        english = "";
        preterit = "";
        past_p = "";
        selected = true;
        nb_fails = 0;
        id = -1;
    }
    public Verbs(String f, String e, String pre, String p_p){
        french = f.trim();
        english = e.trim();
        preterit = pre.trim();
        past_p = p_p.trim();
        selected = true;
        nb_fails = 0;
    }

    public Verbs(String f, String e, String pre, String p_p, int fails, boolean select){
        french = f.trim();
        english = e.trim();
        preterit = pre.trim();
        past_p = p_p.trim();
        selected = select;
        nb_fails = fails;
    }

    public String toString(){
        return french +" \t"+ english +" \t"+ preterit +" \t"+ past_p;
    }

    private boolean field_matched(String field, String to_test){

        // if the field contains multiple possible values,
        if(field.contains("/")){

            // see if the user got one right.
            String[] arr = field.trim().split("/");

            for(int i = 0 ; i < arr.length ; i++){

                if(arr[i].trim().equalsIgnoreCase(to_test.trim())){ 

                    return true;
                }
            }
            return false;
        }
        // otherwise, test for total equality.
        return to_test.trim().equalsIgnoreCase(field.trim());
    }

    public boolean match(String f, String e, String pre, String p_p){
        // if the fields are empty then return false
        if(f == null || f.length() == 0 || e == null || e.length() == 0 || pre == null || pre.length() == 0 || p_p == null || p_p.length() == 0) return false;
        // otherwise check for a match
        if(field_matched(french, f) && field_matched(english, e) && field_matched(preterit, pre) && field_matched(past_p, p_p)) return true;

        return false;
    }
}
