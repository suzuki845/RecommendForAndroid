package com.pin.recommend.ui.home;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.CharacterListActivity;
import com.pin.recommend.R;
import com.pin.recommend.model.Character;
import com.pin.util.RuntimePermissionUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.pin.recommend.Application.REQUEST_PICK_IMAGE;

public class HomeFragment extends Fragment {

    private CircleImageView iconImageView;

    private ImageView backgroundImageView;

    private TextView dateView;

    private TextView elapsedView;

    private Calendar calendar = Calendar.getInstance();

    private Calendar now = Calendar.getInstance();

    private SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    private FloatingActionButton fab;

    private Character character;

    private boolean isEditMode = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
        character = intent.getParcelableExtra(CharacterListActivity.INTENT_CHARACTER);

        iconImageView = root.findViewById(R.id.character_icon);
        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    onSetIcon(null);
                }
            }
        });
        backgroundImageView = root.findViewById(R.id.character_background);
        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode){
                    onSetBackground(null);
                }
            }
        });

        dateView = root.findViewById(R.id.created);
        dateView.setText(FORMAT.format(calendar.getTime()) + "に出会いました");

        elapsedView = root.findViewById(R.id.elapsedTime);
        elapsedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    onShowDatePickerDialog(null);
                }
            }
        });
        elapsedView.setText(Long.toString(character.getDiffDays(now)) + "日");

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEditMode){
                    editMode();
                }else{
                    normalMode();
                }
            }
        });
        return root;
    }

    private void editMode(){
        isEditMode = true;

        GradientDrawable bgShape = new GradientDrawable();
        bgShape.setColor(Color.parseColor("#ffffff"));
        bgShape.setStroke(5, Color.parseColor("#559955"));
        bgShape.setCornerRadius(4f);
        elapsedView.setBackground(bgShape);
        iconImageView.setBorderColor(Color.parseColor("#559955"));
        backgroundImageView.setAlpha((float) 0.5);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_save_24dp, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#ffffff"));
        fab.setImageDrawable(drawable);
    }

    private void normalMode(){
        isEditMode = false;
        elapsedView.setBackground(null);
        iconImageView.setBorderColor(Color.parseColor("#eeeeee"));
        backgroundImageView.setAlpha((float) 1);

        backgroundImageView.setBackgroundColor(Color.parseColor("#332244"));

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mode_edit_24dp, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#ffffff"));
        fab.setImageDrawable(drawable);
    }

    public void onShowDatePickerDialog(View view){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dialog, int year, int month, int dayOfMonth) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                        Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                        !dialog.isShown()) {
                    return;
                    //api19はクリックするとonDateSetが２回呼ばれるため
                }
                Calendar newCalender = Calendar.getInstance();
                newCalender.set(year, month, dayOfMonth);
                Date date = newCalender.getTime();

                calendar.setTime(date);
                dateView.setText(FORMAT.format(date));
            }
        } , year, month, dayOfMonth);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                "キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        datePickerDialog.show();
    }

    private static final int REQUEST_PICK_ICON = 2000;
    public void onSetIcon(View v){
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
            startActivityForResult(intent, REQUEST_PICK_ICON);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_ICON);
        }
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
        if (requestCode == REQUEST_PICK_ICON && resultCode == RESULT_OK) {
            beginCropIcon(result.getData());
            pickMode = REQUEST_PICK_ICON;
        } else if (pickMode == REQUEST_PICK_ICON) {
            handleCropIcon(resultCode, result);
            pickMode = 0;
        }

        if (requestCode == REQUEST_PICK_BACKGROUND && resultCode == RESULT_OK) {
            beginCropBackground(result.getData());
            pickMode = REQUEST_PICK_BACKGROUND;
        } else if (pickMode == REQUEST_PICK_BACKGROUND) {
            handleCropBackground(resultCode, result);
            pickMode = 0;
        }
        super.onActivityResult(requestCode, resultCode, result);
    }

    private void beginCropIcon(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity(), this);
    }

    private void handleCropIcon(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            Bitmap bitmap = BitmapUtility.decodeUri(getActivity(), uri, iconImageView.getWidth(), iconImageView.getHeight());
            iconImageView.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void beginCropBackground(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity(), this);
    }

    private void handleCropBackground(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            Bitmap bitmap = BitmapUtility.decodeUri(getActivity(), uri, backgroundImageView.getWidth(), backgroundImageView.getHeight());
            backgroundImageView.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
