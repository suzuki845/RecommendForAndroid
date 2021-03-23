package com.pin.recommend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.StoryPictureViewModel;
import com.pin.recommend.view.SlideShowItemView;
import com.pin.util.AdMobAdaptiveBannerManager;

import java.util.List;

import static com.pin.recommend.StoryDetailActivity.INTENT_SLIDE_SHOW;
import static com.pin.recommend.StoryDetailActivity.INTENT_SLIDE_SHOW_CURRENT_POSITION;

public class SlideShowActivity extends AppCompatActivity {

    private SliderLayout sliderView;
    private Story story;
    private int initPosition = 0;

    private AccountViewModel accountViewModel;
    private StoryPictureViewModel storyPictureViewModel;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        adViewContainer = this.findViewById(R.id.ad_container);
        adMobManager = new AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id));
        adMobManager.testMode(false);
        adMobManager.setAllowAdClickLimit(6);
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3);
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14);

        accountViewModel = MyApplication.getAccountViewModel(this);
        storyPictureViewModel = new ViewModelProvider(this).get(StoryPictureViewModel.class);

        story = getIntent().getParcelableExtra(INTENT_SLIDE_SHOW);
        initPosition = getIntent().getIntExtra(INTENT_SLIDE_SHOW_CURRENT_POSITION, 0);

        sliderView = findViewById(R.id.slider);
        sliderView.stopAutoCycle();

        accountViewModel.getAccountLiveData().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                initializeToolbar(account);
            }
        });

        storyPictureViewModel.findByTrackedStoryId(story.id).observe(this, new Observer<List<StoryPicture>>() {
            @Override
            public void onChanged(List<StoryPicture> storyPictures) {
                for(StoryPicture storyPicture : storyPictures){
                    SlideShowItemView sliderItem = new SlideShowItemView(
                            SlideShowActivity.this, storyPicture.getBitmap(SlideShowActivity.this,500, 500) );
                    sliderView.addSlider(sliderItem);
                }
                sliderView.setCurrentPosition(initPosition);
            }
        });
    }

    private void initializeToolbar(Account account){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(account.getToolbarBackgroundColor());
        toolbar.setTitleTextColor(account.getToolbarTextColor());
        Drawable drawable = DrawableCompat.wrap(toolbar.getOverflowIcon());
        DrawableCompat.setTint(drawable, account.getToolbarTextColor());
        MyApplication.setupStatusBarColor(this , account.getToolbarTextColor(), account.getToolbarBackgroundColor());
        toolbar.setTitle("スライドショー");
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume(){
        super.onResume();
        adMobManager.checkAndLoad();
    }


}
