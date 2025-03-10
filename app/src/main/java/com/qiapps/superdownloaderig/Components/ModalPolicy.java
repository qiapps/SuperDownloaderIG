package com.qiapps.superdownloaderig.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.qiapps.superdownloaderig.R;

public class ModalPolicy {

    public static void show(Activity activity, final View.OnClickListener callback){

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        View cont = activity.getLayoutInflater().inflate(R.layout.modal_policy, null);
        dialog.setView(cont);
        final AlertDialog mDialog = dialog.create();

        cont.findViewById(R.id.btn_entendi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                mDialog.cancel();
                callback.onClick(null);
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callback.onClick(null);
            }
        });

        mDialog.show();

    }


}
