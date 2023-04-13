package com.example.teachmeio;

public class Verbs {
    public String french;
    public String english;
    public String preterit;
    public String past_p;

    public boolean selected;

    public Verbs(){
        french = "";
        english = "";
        preterit = "";
        past_p = "";
        selected = true;
    }
    public Verbs(String f, String e, String pre, String p_p){
        french = f;
        english = e;
        preterit = pre;
        past_p = p_p;
        selected = true;
    }

    public String toString(){
        return french +" \t"+ english +" \t"+ preterit +" \t"+ past_p;
    }
}
