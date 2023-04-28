package com.example.teachmeio;

import java.util.Comparator;

public class VerbComparator implements Comparator<Verbs> {
    @Override
    public int compare(Verbs v1, Verbs v2) {
        return v1.nb_fails - v2.nb_fails;
    }
}
