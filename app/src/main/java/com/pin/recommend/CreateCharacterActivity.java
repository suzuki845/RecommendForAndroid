package com.pin.recommend;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel;
import com.pin.util.AdMobAdaptiveBannerManager;
import com.pin.util.Reward;
import com.pin.util.RuntimePermissionUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateCharacterActivity extends AppCompatActivity {

    private CircleImageView iconImageView;
    private Bitmap iconImageBitmap;
    private TextView dateView;
    private EditText characterNameView;
    private FloatingActionButton fab;

    private Date createdData = new Date();

    private Calendar calendar = Calendar.getInstance();

    private SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");


    private AccountViewModel accountViewModel;
    private RecommendCharacterViewModel characterViewModel;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);

        adViewContainer = this.findViewById(R.id.ad_container);
        adMobManager = new AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id));
        adMobManager.setAllowAdClickLimit(6);
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3);
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14);
        Reward reward = Reward.Companion.getInstance(this);
        reward.isBetweenRewardTime().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isBetweenRewardTime) {
                adMobManager.setEnable(!isBetweenRewardTime);
                adMobManager.checkFirst();
            }
        });


        accountViewModel = MyApplication.getAccountViewModel(this);
        characterViewModel = new ViewModelProvider(this).get(RecommendCharacterViewModel.class);

        iconImageView = findViewById(R.id.character_icon);
        dateView = findViewById(R.id.created);
        characterNameView = findViewById(R.id.character_name);
        fab = findViewById(R.id.fab);

        dateView.setText(FORMAT.format(calendar.getTime()));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateCharacter();
            }
        });

        toolbar = findViewById(R.id.toolbar);

        LiveData<Account> accountLiveData = accountViewModel.getAccountLiveData();
        accountLiveData.observe(this, new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                initializeToolbar(account);
            }
        });
    }

    private void initializeToolbar(Account account){
        setSupportActionBar(toolbar);
    }

    public void onCreateCharacter(){
        RecommendCharacter character = new RecommendCharacter();
        character.accountId = accountViewModel.getAccountLiveData().getValue().id;
        character.created = createdData;
        character.name = characterNameView.getText().toString();
        if(iconImageBitmap != null) {
            character.saveIconImage(this, iconImageBitmap);
        }
        characterViewModel.insert(character);

        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        adMobManager.checkAndLoad();
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
                createdData = date;
            }
        } , year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

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

        getIntent().putExtra(Constants.PICK_IMAGE, true);

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
            iconImageBitmap = bitmap;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
