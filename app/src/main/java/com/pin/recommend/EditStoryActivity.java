package com.pin.recommend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.adapter.PickStoryPictureAdapter;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.StoryPictureViewModel;
import com.pin.recommend.model.viewmodel.StoryViewModel;
import com.pin.util.AdMobAdaptiveBannerManager;
import com.pin.util.RuntimePermissionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.pin.recommend.MyApplication.REQUEST_PICK_IMAGE;
import static com.pin.recommend.StoryDetailActivity.INTENT_EDIT_STORY;

public class EditStoryActivity extends AppCompatActivity {

    private ImageView pickImageView;

    private PickStoryPictureAdapter pickStoryPictureAdapter;
    private RecyclerView recyclerView;

    private EditText editCommentView;

    private TextView createdView;
    private Date created = new Date();

    private Story story;

    private AccountViewModel accountViewModel;
    private StoryViewModel storyViewModel;
    private StoryPictureViewModel storyPictureViewModel;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);

        adViewContainer = this.findViewById(R.id.ad_container);
        adMobManager = new AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id));
        adMobManager.testMode(false);
        adMobManager.setAllowAdClickLimit(6);
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3);
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14);

        accountViewModel = MyApplication.getAccountViewModel(this);
        storyViewModel = new ViewModelProvider(this).get(StoryViewModel.class);
        storyPictureViewModel = new ViewModelProvider(this).get(StoryPictureViewModel.class);

        story = getIntent().getParcelableExtra(INTENT_EDIT_STORY);

        createdView = findViewById(R.id.created);
        pickImageView = findViewById(R.id.pickImage);
        editCommentView = findViewById(R.id.comment);
        recyclerView = findViewById(R.id.recycler_view);

        pickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickImage(null);
            }
        });

        pickStoryPictureAdapter = new PickStoryPictureAdapter(this);
        pickStoryPictureAdapter.setCanDelete(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setAdapter(pickStoryPictureAdapter);
        editCommentView.setText(story.comment);

        storyViewModel.findByTrackedId(story.id).observe(this, new Observer<Story>() {
            @Override
            public void onChanged(Story story) {
                editCommentView.setText(story.comment);
                created = story.created;
                createdView.setText(FORMAT.format(story.created));
            }
        });

        storyPictureViewModel.findByTrackedStoryId(story.id).observe(this, new Observer<List<StoryPicture>>() {
            @Override
            public void onChanged(List<StoryPicture> storyPictures) {
                List<Bitmap> images = new ArrayList<>();
                for (StoryPicture storyPicture : storyPictures){
                    images.add(storyPicture.getBitmap(EditStoryActivity.this, 500, 500));
                }
                pickStoryPictureAdapter.setList(images);
            }
        });

        toolbar = findViewById(R.id.toolbar);

        accountViewModel.getAccountLiveData().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                initializeToolbar(account);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        adMobManager.checkAndLoad();
    }

    private void initializeToolbar(Account account){
        toolbar.setBackgroundColor(account.getToolbarBackgroundColor());
        toolbar.setTitleTextColor(account.getToolbarTextColor());
        Drawable drawable = DrawableCompat.wrap(toolbar.getOverflowIcon());
        DrawableCompat.setTint(drawable, account.getToolbarTextColor());
        MyApplication.setupStatusBarColor(this ,
                account.getToolbarTextColor(),
                account.getToolbarBackgroundColor());
        toolbar.setTitle("?????????????????????");
        setSupportActionBar(toolbar);
    }

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy???MM???dd???");
    public void onShowDatePickerDialog(View v) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(story.created);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker dialog, int year, int month, int dayOfMonth) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                        !dialog.isShown()) {
                    return;
                }
                Calendar newCalender = Calendar.getInstance();
                newCalender.set(year, month, dayOfMonth);
                Date date = newCalender.getTime();
                createdView.setText(FORMAT.format(date));
                created = date;
            }
        }, year, month, dayOfMonth);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private static final int REQUEST_PICK_STORY_PICTURE = 3000;
    public void onPickImage(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(this.getFragmentManager(),
                            "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
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
            startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Bitmap bitmap = BitmapUtility.decodeUri(this, uri, 500, 500);
                int count = pickStoryPictureAdapter.getItemCount();
                if(count >= 3){
                    Toast.makeText(EditStoryActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickStoryPictureAdapter.add(bitmap);
            }
        }

        getIntent().putExtra(Constants.PICK_IMAGE, true);

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_story, menu);
        return true;
    }

    private void save(){
        String comment = editCommentView.getText().toString();
        story.comment = comment;
        story.created = created;
        storyViewModel.updateWithPicture(story, new StoryViewModel.WithSavePicture() {
            @Override
            public void onSave(long storyId) {
                for(StoryPicture sp : storyPictureViewModel.findByStoryId(storyId)){
                    sp.deleteImage(EditStoryActivity.this);
                    storyPictureViewModel.delete(sp);
                }
                List<Bitmap> pictures = pickStoryPictureAdapter.getList();
                for(Bitmap bitmap : pictures){
                    StoryPicture storyPicture = new StoryPicture();
                    storyPicture.storyId = story.id;
                    storyPicture.saveImage(EditStoryActivity.this, bitmap);
                    storyPictureViewModel.insert(storyPicture);
                }
            }
        });

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
        }
        return true;
    }



}
