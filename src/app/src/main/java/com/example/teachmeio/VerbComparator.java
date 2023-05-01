package com.example.teachmeio;

import java.util.Comparator;

/*
    VerbComparator is a helper class created to be able to use typical comparison signs
    between two verbs objects.
 */
public class VerbComparator implements Comparator<Verbs> {
    @Override
    public int compare(Verbs v1, Verbs v2) {
        return v1.nb_fails - v2.nb_fails;
    }
}
