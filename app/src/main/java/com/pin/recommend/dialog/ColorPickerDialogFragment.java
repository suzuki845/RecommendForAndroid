package com.pin.recommend.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.pin.recommend.R;

public class ColorPickerDialogFragment extends DialogFragment {

    private DialogActionListener<ColorPickerDialogFragment> actionListener;

    public ColorPickerDialogFragment(DialogActionListener<ColorPickerDialogFragment> listener){
        this.actionListener = listener;
    }

    private int defaultColor = Color.BLACK;
    public void setDefaultColor(int color){
        defaultColor = color;
    }

    public static final String TAG = "com.pin.recommend.ColorPickerDialogFragment";

    private ColorPickerView colorPickerView;

    public int getColor(){
        return colorPickerView.getColor();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_color_picker, null);
        colorPickerView = root.findViewById(R.id.color_picker);
        colorPickerView.setAlphaSliderVisible(true);
        colorPickerView.setAlphaSliderText("透明度");
        colorPickerView.setColor(defaultColor);
        builder.setView(root)
                .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onDecision(ColorPickerDialogFragment.this);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onCancel();
                    }
                });

        return builder.create();
    }


}
