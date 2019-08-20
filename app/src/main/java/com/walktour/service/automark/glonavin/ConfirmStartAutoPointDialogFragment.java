package com.walktour.service.automark.glonavin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.walktour.gui.R;

/**
 * Created by Yi.Lin on 2018/5/12.
 * 是否开始自动打点对话框
 */

public class ConfirmStartAutoPointDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.glonavin_auto_mark_is_confirm_start))
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GlonavinDataManager.getInstance().setHasDirectionSet(true);
                    }
                }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlonavinDataManager.getInstance().clearPoint();
            }
        });
        setCancelable(false);
        return builder.create();
    }
}
