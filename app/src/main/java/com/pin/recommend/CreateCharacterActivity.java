package com.pin.recommend;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.imageutil.BitmapUtility;
import com.pin.util.RuntimePermissionUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateCharacterActivity extends AppCompatActivity {

    private CircleImageView iconImageView;

    private ImageView backgroundImageView;

    private TextView dateView;

    private Calendar calendar = Calendar.getInstance();

    private SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iconImageView = findViewById(R.id.character_icon);
        backgroundImageView = findViewById(R.id.character_background);
        dateView = findViewById(R.id.created);
        dateView.setText(FORMAT.format(calendar.getTime()));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onShowDatePickerDialog(View view){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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

    private static final int REQUEST_PICK_ICON = 1000;
    public void onSetIcon(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(getFragmentManager(),
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください");
                    return;
                }else{
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PICK_ICON);
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

    private static final int REQUEST_PICK_BACKGROUND = 1001;
    public void onSetBackground(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(getFragmentManager(),
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください");
                    return;
                }else{
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PICK_BACKGROUND);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
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
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCropIcon(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            Bitmap bitmap = BitmapUtility.decodeUri(this, uri, iconImageView.getWidth(), iconImageView.getHeight());
            iconImageView.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void beginCropBackground(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).start(this);
    }

    private void handleCropBackground(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            Bitmap bitmap = BitmapUtility.decodeUri(this, uri, backgroundImageView.getWidth(), backgroundImageView.getHeight());
            backgroundImageView.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
