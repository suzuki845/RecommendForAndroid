package com.pin.recommend;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pin.recommend.dialog.BackgroundSettingDialogFragment;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.dialog.ToolbarSettingDialogFragment;
import com.pin.recommend.main.SectionsPagerAdapter;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel;
import com.pin.util.AdMobAdaptiveBannerManager;


public class CharacterDetailActivity extends AppCompatActivity {

    public static final String INTENT_CHARACTER = "com.pin.recommend.CharacterListFragment.INTENT_CHARACTER";


    private View background;

    private TabLayout tabs;

    private AccountViewModel accountViewModel;
    private RecommendCharacterViewModel characterViewModel;

    private RecommendCharacter character;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

        adViewContainer = this.findViewById(R.id.ad_container);
        adMobManager = new AdMobAdaptiveBannerManager(this, adViewContainer, getString(R.string.ad_unit_id));
        adMobManager.testMode(false);
        adMobManager.setAllowAdClickLimit(6);
        adMobManager.setAllowRangeOfAdClickByTimeAtMinute(3);
        adMobManager.setAllowAdLoadByElapsedTimeAtMinute(24 * 60 * 14);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        background = findViewById(R.id.background);

        accountViewModel = MyApplication.getAccountViewModel(this);
        characterViewModel = new ViewModelProvider(this).get(RecommendCharacterViewModel.class);

        character = getIntent().getParcelableExtra(INTENT_CHARACTER);

        initializeBackground(character);
        initializeTab(character);
        initializeToolbar(character);

        LiveData<RecommendCharacter> characterLiveData = characterViewModel.getCharacter(character.id);
        characterLiveData.observe(this, new Observer<RecommendCharacter>() {
            @Override
            public void onChanged(RecommendCharacter character) {
                if(character == null) return;
                initializeBackground(character);
                initializeTab(character);
                initializeToolbar(character);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        adMobManager.checkAndLoad();
    }

    private void initializeBackground(RecommendCharacter character){
        background.setBackground(character.getBackgroundDrawable(CharacterDetailActivity.this, 700, 700));
    }

    private void initializeTab(RecommendCharacter character){
        tabs.setTabTextColors(ColorStateList.valueOf(character.getHomeTextColor()));
    }

    private void initializeToolbar(RecommendCharacter character){
        Account account = accountViewModel.getAccount().getValue();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(character.getToolbarBackgroundColor(this, account.getToolbarBackgroundColor()));
        toolbar.setTitleTextColor(character.getToolbarTextColor(this, account.getToolbarTextColor()));
        Drawable drawable = DrawableCompat.wrap(toolbar.getOverflowIcon());
        DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, account.getToolbarTextColor()));
        MyApplication.setupStatusBarColor(this,
                character.getToolbarTextColor(this, accountViewModel.getAccount().getValue().getToolbarTextColor()),
                character.getToolbarBackgroundColor(this, accountViewModel.getAccount().getValue().getToolbarBackgroundColor()));
        toolbar.setTitle(character.name);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem deleteBackgroundImage = menu.findItem(R.id.delete_background_image);
        if(!character.hasBackgroundImage()){
            deleteBackgroundImage.setVisible(false);
        }else{
            deleteBackgroundImage.setVisible(true);
        }

        MenuItem item = menu.findItem(R.id.fix_home);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Account account = accountViewModel.getAccount().getValue();
                if(account.fixedCharacterId != null) {
                    item.setIcon(R.drawable.pin_outline);
                    account.removeFixedCharacter(CharacterDetailActivity.this);
                    accountViewModel.saveAccount(account);
                }else{
                    item.setIcon(R.drawable.pin_fill);
                    account.setFixedCharacter(CharacterDetailActivity.this, character.id);
                    accountViewModel.saveAccount(account);
                    Toast.makeText(CharacterDetailActivity.this, "トップページ に固定しました", Toast.LENGTH_SHORT).show();
                }
                setMenuItemIconTint(item);
                return false;
            }
        });
        Account account = accountViewModel.getAccount().getValue();
        if(account.getFixedCharacterId(this) == null) {
            item.setIcon(R.drawable.pin_outline);
        }else{
            item.setIcon(R.drawable.pin_fill);
        }
        setAllMenuItemIconTint(menu);
        return true;
    }

    private void setMenuItemIconTint(MenuItem item){
        Account account = accountViewModel.getAccount().getValue();
        Drawable drawable = item.getIcon();
        drawable = DrawableCompat.wrap(drawable);

        DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, account.getToolbarTextColor()));
    }

    private void setAllMenuItemIconTint(Menu menu){
        Account account = accountViewModel.getAccount().getValue();
        for (int i = 0;i < menu.size();i++) {
            MenuItem item = menu.getItem(i);
            Drawable drawable = item.getIcon();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, character.getToolbarTextColor(this, account.getToolbarTextColor()));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Account account = accountViewModel.getAccount().getValue();

        switch (item.getItemId()) {
            case R.id.change_background_image:
                BackgroundSettingDialogFragment backgroundSettingDialogFragment = new BackgroundSettingDialogFragment(new DialogActionListener<BackgroundSettingDialogFragment>() {
                    @Override
                    public void onDecision(BackgroundSettingDialogFragment dialog) {
                        if(dialog.getBackgroundImage() != null) {
                            character.saveBackgroundImage(CharacterDetailActivity.this, dialog.getBackgroundImage());
                        }
                        character.backgroundColor = dialog.getBackgroundColor();
                        characterViewModel.update(character);
                    }
                    @Override
                    public void onCancel() {

                    }
                });
                backgroundSettingDialogFragment.setDefaultBackgroundImage(character.getBackgroundBitmap(this, 300, 300));
                backgroundSettingDialogFragment.setDefaultBackgroundColor(character.getBackgroundColor());
                backgroundSettingDialogFragment.show(getSupportFragmentManager(), BackgroundSettingDialogFragment.TAG);
                return true;
            case R.id.setting_toolbar:
                ToolbarSettingDialogFragment toolbarSettingDialogFragment = new ToolbarSettingDialogFragment(new DialogActionListener<ToolbarSettingDialogFragment>() {
                    @Override
                    public void onDecision(ToolbarSettingDialogFragment dialog) {
                        character.toolbarBackgroundColor = dialog.getBackgroundColor();
                        character.toolbarTextColor = dialog.getTextColor();
                        characterViewModel.update(character);
                    }
                    @Override
                    public void onCancel() {

                    }
                });
                toolbarSettingDialogFragment.setDefaultBackgroundColor(character.getToolbarBackgroundColor(this, account.getToolbarBackgroundColor()));
                toolbarSettingDialogFragment.setDefaultTextColor(character.getToolbarTextColor(this, account.getToolbarTextColor()));
                toolbarSettingDialogFragment.show(getSupportFragmentManager(), ToolbarSettingDialogFragment.TAG);
                return true;
            case R.id.delete_background_image:
                character.deleteImageBackgroundImage(this);
                characterViewModel.update(character);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        Account account = accountViewModel.getAccount().getValue();
        if(account.getFixedCharacterId(this) != null){
            moveTaskToBack (true);
        }else{
            super.onBackPressed();
        }
    }

}

