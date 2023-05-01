package com.example.teachmeio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import java.util.zip.Inflater;

public class LoadingDialog {

    // declare our activity and context object references.
    public Activity activity;
    public Context context;
    public AlertDialog dialog;

    LoadingDialog(Activity activity, Context context){
        // populate our objects with the caller view context info.
        this.activity = activity;
        this.context = context;
    }

    public void startLoadingDialog(){
        // build the alert dialog on the caller activity
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

        // target the caller activity layout
        LayoutInflater inflater = activity.getLayoutInflater();

        // inject the loading dialog box layout to the caller activity layout.
        builder.setView(inflater.inflate(R.layout.custom_dialog, null));

        // make the dialog box not cancelable by user events.
        builder.setCancelable(false);

        // create and display the loading box
        dialog = builder.create();
        dialog.show();
    }

    // add a dismiss method to stop the loading box show.
    public void dismissDialog(){
        dialog.dismiss();
    }

}
