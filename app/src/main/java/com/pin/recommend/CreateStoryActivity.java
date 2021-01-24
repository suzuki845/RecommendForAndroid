package com.pin.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.adapter.PickStoryPictureAdapter;
import com.pin.recommend.main.StoryListFragment;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.StoryPictureViewModel;
import com.pin.recommend.model.viewmodel.StoryViewModel;
import com.pin.util.AdMobAdaptiveBannerManager;
import com.pin.util.RuntimePermissionUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import static com.pin.recommend.MyApplication.REQUEST_PICK_IMAGE;

public class CreateStoryActivity extends AppCompatActivity {

    private ImageView pickImageView;

    private PickStoryPictureAdapter pickStoryPictureAdapter;
    private RecyclerView recyclerView;

    private RecommendCharacter character;

    private EditText editCommentView;

    private AccountViewModel accountViewModel;
    private StoryViewModel storyViewModel;
    private StoryPictureViewModel storyPictureViewModel;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        adViewContainer = this.findViewById(R.id.ad_container);
        adMobManager = new AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id));
        adMobManager.testMode(false);
        adMobManager.setAllowAdClickLimit(6);
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3);
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14);

        accountViewModel = MyApplication.getAccountViewModel(this);
        storyViewModel = new ViewModelProvider(this).get(StoryViewModel.class);
        storyPictureViewModel = new ViewModelProvider(this).get(StoryPictureViewModel.class);

        character = getIntent().getParcelableExtra(StoryListFragment.INTENT_CREATE_STORY);

        editCommentView = findViewById(R.id.comment);
        pickImageView = findViewById(R.id.pickImage);
        recyclerView = findViewById(R.id.recycler_view);

        pickStoryPictureAdapter = new PickStoryPictureAdapter(this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setAdapter(pickStoryPictureAdapter);


        pickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickImage(null);
            }
        });

        accountViewModel.getAccount().observe(this, new Observer<Account>() {
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(account.getToolbarBackgroundColor());
        toolbar.setTitleTextColor(account.getToolbarTextColor());
        Drawable drawable = DrawableCompat.wrap(toolbar.getOverflowIcon());
        DrawableCompat.setTint(drawable, account.getToolbarTextColor());
        MyApplication.setupStatusBarColor(this ,
                account.getToolbarTextColor(),
                account.getToolbarBackgroundColor());
        setSupportActionBar(toolbar);
    }

    private static final int REQUEST_PICK_STORY_PICTURE = 3000;
    public void onPickImage(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(this.getFragmentManager(),
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください");
                    System.out.println("onPickImage 1");
                    return;
                }else{
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PICK_IMAGE);
                    System.out.println("onPickImage 2");
                    return;
                }
            }
        }

        System.out.println("onPickImage 3");
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
                    Toast.makeText(CreateStoryActivity.this, "３つ以上は選択出来ません", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickStoryPictureAdapter.add(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_story, menu);
        return true;
    }

    private void save(){
        String comment = editCommentView.getText().toString();
        Story story = new Story();
        story.characterId = character.id;
        story.comment = comment;
        story.created = new Date();
        storyViewModel.insertStoryWithPicture(story, new StoryViewModel.WithSavePicture() {
            @Override
            public void onSave(long insertId) {
                List<Bitmap> pictures = pickStoryPictureAdapter.getList();
                for(Bitmap bitmap : pictures){
                    StoryPicture storyPicture = new StoryPicture();
                    storyPicture.storyId = insertId;
                    storyPicture.saveImage(CreateStoryActivity.this, bitmap);
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
