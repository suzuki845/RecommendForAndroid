package com.pin.recommend.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.R;
import com.pin.util.DisplaySizeCheck;
import com.pin.util.RuntimePermissionUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.pin.recommend.MyApplication.REQUEST_PICK_IMAGE;

public class BackgroundSettingDialogFragment extends DialogFragment {

    public static final String TAG = "com.pin.recommend.BackgroundSettingDialogFragment";

    private ImageView backgroundImageView;
    private View backgroundColorView;

    private Button backgroundImageButton;
    private Button backgroundColorButton;

    private DialogActionListener<BackgroundSettingDialogFragment> actionListener;

    public BackgroundSettingDialogFragment(DialogActionListener<BackgroundSettingDialogFragment> listener){
        this.actionListener = listener;
    }

    private Bitmap backgroundImage;
    public void setDefaultBackgroundImage(Bitmap bitmap){
        backgroundImage = bitmap;
    }
    public Bitmap getBackgroundImage(){return backgroundImage;}

    private int backgroundColor;
    public void setDefaultBackgroundColor(int color){
        backgroundColor = color;
    }

    public int getBackgroundColor(){
        return backgroundColor;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("背景の設定");
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_background_setting, null);
        backgroundImageView = root.findViewById(R.id.preview_background_image);
        backgroundImageView.setImageBitmap(backgroundImage);
        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), backgroundImageView);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.pic_story_picture_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove:
                                backgroundImage = null;
                                backgroundImageView.setImageBitmap(null);
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        backgroundImageButton = root.findViewById(R.id.background_image_button);
        backgroundImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetBackground(null);
            }
        });
        backgroundColorView = root.findViewById(R.id.preview_background_color);
        backgroundColorView.setBackgroundColor(backgroundColor);
        backgroundColorButton = root.findViewById(R.id.background_color_button);
        backgroundColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogFragment dialog = new ColorPickerDialogFragment(new DialogActionListener<ColorPickerDialogFragment>() {
                    @Override
                    public void onDecision(ColorPickerDialogFragment dialog) {
                        backgroundColorView.setBackgroundColor(dialog.getColor());
                        backgroundColor = dialog.getColor();
                    }
                    @Override
                    public void onCancel() {

                    }
                });
                dialog.setDefaultColor(backgroundColor);
                dialog.show(getActivity().getSupportFragmentManager(), ColorPickerDialogFragment.TAG);
            }
        });
        builder.setView(root)
                .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onDecision(BackgroundSettingDialogFragment.this);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actionListener.onCancel();
                    }
                });

        return builder.create();
    }


    private static final int REQUEST_PICK_BACKGROUND = 2001;
    public void onSetBackground(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(getActivity().getFragmentManager(),
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください");
                    return;
                }else{
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PICK_IMAGE);
                    return;
                }
            }
        }

        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_BACKGROUND);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_BACKGROUND);
        }

    }

    private int pickMode = 0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_PICK_BACKGROUND && resultCode == RESULT_OK) {
            beginCropBackground(result.getData());
            pickMode = REQUEST_PICK_BACKGROUND;
        } else if (pickMode == REQUEST_PICK_BACKGROUND) {
            handleCropBackground(resultCode, result);
            pickMode = 0;
        }
        super.onActivityResult(requestCode, resultCode, result);
    }

    private void beginCropBackground(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Point displaySize = DisplaySizeCheck.getDisplaySize(getActivity());
        Crop.of(source, destination).withAspect(displaySize.x, displaySize.y).start(getActivity(), this);
    }

    private void handleCropBackground(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            Bitmap bitmap = BitmapUtility.decodeUri(getActivity(), uri);
            backgroundImageView.setImageBitmap(bitmap);
            backgroundImage = bitmap;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}
