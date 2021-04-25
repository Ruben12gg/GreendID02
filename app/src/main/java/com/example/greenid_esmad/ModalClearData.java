package com.example.greenid_esmad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;

 class ModelClearData {

     Context context;

}

public class ModalClearData extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // "Build" dialog with Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Pressing 'Yes' will delete all the application cache data stored in your phone. This will immediately close the app and require you to Log In next time you launch it. ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("DELETE", "Deleting cache!!");

                            //Run a java Android command that clears the cache from current app, parsing this app package name
                            try {
                                Runtime runtime = Runtime.getRuntime();
                                runtime.exec("pm clear " + getContext().getPackageName() + " HERE");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}


