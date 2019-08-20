package com.walktour.service.automark.glonavin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.walktour.gui.R;


/**
 * Created by Yi.Lin on 2018/5/11.
 * 提醒用户设定起始点和行进方向dialog
 */

public class GlonavinSetDirectionTipDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.glonavin_auto_mark_set_start_point_and_direction))
                .setPositiveButton(R.string.str_ok, null);
        setCancelable(false);
        return builder.create();
    }
}
