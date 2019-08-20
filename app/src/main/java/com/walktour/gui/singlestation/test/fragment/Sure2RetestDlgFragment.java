package com.walktour.gui.singlestation.test.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.walktour.gui.R;

import butterknife.ButterKnife;

/**
 * Created by yi.lin on 2017/11/01.
 * <p>
 * 点击测试按钮时如果有未上传测试记录时弹出的对话框
 */

public class Sure2RetestDlgFragment extends DialogFragment {

    private final static String TAG = "Sure2RetestDlgFragment";


    private Dialog.OnClickListener mOnClickListener;

    public Dialog.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(Dialog.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dlg_fragment_singlestation_sure_to_retest, null);
        ButterKnife.bind(this, view);
        builder.setTitle(R.string.str_tip).setView(view).setPositiveButton(R.string.single_station_sure_to_retest,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(null!=mOnClickListener){
                            mOnClickListener.onClick(dialog,id);
                        }
                    }
                }).setNegativeButton(com.walktour.base.R.string.control_cancel, null);
        return builder.create();
    }




}
