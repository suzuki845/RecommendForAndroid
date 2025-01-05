package com.pin.recommend.ui.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pin.recommend.R;

public class ToolbarSettingDialogFragment extends DialogFragment {

    public static final String TAG = "com.pin.recommend.ToolbarSettingDialogFragment";

    private DialogActionListener<ToolbarSettingDialogFragment> actionListener;

    public ToolbarSettingDialogFragment(DialogActionListener<ToolbarSettingDialogFragment> listener) {
        this.actionListener = listener;
    }

    private int backgroundColor = Color.parseColor("#eb34ab");

    public void setDefaultBackgroundColor(int color) {
        backgroundColor = color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    private int textColor = Color.WHITE;

    public void setDefaultTextColor(int color) {
        textColor = color;
    }

    public int getTextColor() {
        return textColor;
    }

    private View previewBackgroundColor;
    private View previewTextColor;
    private Button toolbarBackgroundColorView;
    private Button toolbarTextColorView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ツールバーの設定");
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_toolbar_setting, null);
        previewBackgroundColor = root.findViewById(R.id.preview_background_color);
        previewBackgroundColor.setBackgroundColor(backgroundColor);
        toolbarBackgroundColorView = root.findViewById(R.id.toolbar_background_color_button);
        toolbarBackgroundColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogFragment dialog = new ColorPickerDialogFragment(new DialogActionListener<ColorPickerDialogFragment>() {
                    @Override
                    public void onDecision(ColorPickerDialogFragment dialog) {
                        previewBackgroundColor.setBackgroundColor(dialog.getColor());
                        backgroundColor = dialog.getColor();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                dialog.setDefaultColor(backgroundColor);
                dialog.show(getActivity().getSupportFragmentManager(), ColorPickerDialogFragment.TAG);
                ;
            }
        });
        previewTextColor = root.findViewById(R.id.preview_text_color);
        previewTextColor.setBackgroundColor(textColor);
        toolbarTextColorView = root.findViewById(R.id.toolbar_text_color_button);
        toolbarTextColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogFragment dialog = new ColorPickerDialogFragment(new DialogActionListener<ColorPickerDialogFragment>() {
                    @Override
                    public void onDecision(ColorPickerDialogFragment dialog) {
                        previewTextColor.setBackgroundColor(dialog.getColor());
                        textColor = dialog.getColor();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                dialog.setDefaultColor(textColor);
                dialog.show(getActivity().getSupportFragmentManager(), ColorPickerDialogFragment.TAG);
            }
        });
        builder.setView(root)
                .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onDecision(ToolbarSettingDialogFragment.this);
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
