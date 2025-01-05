package com.pin.recommend.ui.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {

    public static final String Tag = "com.pin.reccomend.DeleteDialogFragment";

    private DialogActionListener<DeleteDialogFragment> actionListener;

    public DeleteDialogFragment(DialogActionListener<DeleteDialogFragment> listener) {
        this.actionListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("本当に削除しますか？")
                .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onDecision(DeleteDialogFragment.this);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onCancel();
                    }
                })
                .create();
    }


    @Override
    public void onPause() {
        super.onPause();
        // onPause でダイアログを閉じる場合
        dismiss();
    }
}
