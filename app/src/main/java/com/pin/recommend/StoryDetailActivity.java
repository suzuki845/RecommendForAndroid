package com.pin.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.pin.recommend.adapter.PickStoryPictureAdapter;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.StoryPictureViewModel;
import com.pin.recommend.model.viewmodel.StoryViewModel;
import com.pin.util.AdMobAdaptiveBannerManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.pin.recommend.main.StoryListFragment.INTENT_STORY;

public class StoryDetailActivity extends AppCompatActivity {

    public static final String INTENT_SLIDE_SHOW = "com.pin.recommend.StoryActivity.INTENT_SLIDE_SHOW";
    public static final String INTENT_SLIDE_SHOW_CURRENT_POSITION = "com.pin.recommend.StoryActivity.INTENT_SLIDE_SHOW_CURRENT_POSITION";
    public static final String INTENT_EDIT_STORY = "com.pin.recommend.StoryFragment.INTENT_EDIT_STORY";

    private PickStoryPictureAdapter viewStoryPictureAdapter;
    private RecyclerView recyclerView;
    private TextView comment;

    private Story story;

    private AccountViewModel accountViewModel;
    private StoryViewModel storyViewModel;
    private StoryPictureViewModel storyPictureViewModel;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        adViewContainer = this.findViewById(R.id.ad_container);
        adMobManager = new AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id));
        adMobManager.testMode(false);
        adMobManager.setAllowAdClickLimit(6);
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3);
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14);

        accountViewModel = MyApplication.getAccountViewModel(this);
        storyPictureViewModel = new ViewModelProvider(this).get(StoryPictureViewModel.class);
        storyViewModel = new ViewModelProvider(this).get(StoryViewModel.class);

        story = getIntent().getParcelableExtra(INTENT_STORY);

        comment = findViewById(R.id.comment);
        recyclerView = findViewById(R.id.recycler_view);

        viewStoryPictureAdapter = new PickStoryPictureAdapter(this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setAdapter(viewStoryPictureAdapter);
        comment.setText(story.comment);

        viewStoryPictureAdapter.setOnClickListener(new PickStoryPictureAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(StoryDetailActivity.this, SlideShowActivity.class);
                intent.putExtra(INTENT_SLIDE_SHOW, story);
                intent.putExtra(INTENT_SLIDE_SHOW_CURRENT_POSITION, position);
                startActivity(intent);
            }
        });


        storyViewModel.findByTrackedId(story.id).observe(this, new Observer<Story>() {
            @Override
            public void onChanged(Story story) {
                comment.setText(story.comment);
            }
        });

        storyPictureViewModel.findByTrackedStoryId(story.id).observe(this, new Observer<List<StoryPicture>>() {
            @Override
            public void onChanged(List<StoryPicture> storyPictures) {
                List<Bitmap> images = new ArrayList<>();
                for (StoryPicture storyPicture : storyPictures){
                    images.add(storyPicture.getBitmap(StoryDetailActivity.this, 500, 500));
                }
                viewStoryPictureAdapter.setList(images);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.edit_mode:
                Intent intent = new Intent(StoryDetailActivity.this, EditStoryActivity.class);
                intent.putExtra(INTENT_EDIT_STORY, story);
                startActivity(intent);
                break;
        }
        return true;
    }


    private void initializeToolbar(Account account){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(account.getToolbarBackgroundColor());
        toolbar.setTitleTextColor(account.getToolbarTextColor());
        Drawable drawable = DrawableCompat.wrap(toolbar.getOverflowIcon());
        DrawableCompat.setTint(drawable, account.getToolbarTextColor());
        MyApplication.setupStatusBarColor(this , account.getToolbarTextColor(), account.getToolbarBackgroundColor());
        toolbar.setTitle("ストーリー");
        setSupportActionBar(toolbar);
    }



}
