package com.pin.recommend;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.recommend.adapter.CharactersAdapter;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.dialog.ToolbarSettingDialogFragment;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.model.viewmodel.EditStateViewModel;
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel;
import com.pin.util.AdMobAdaptiveBannerManager;
import com.pin.util.Reward;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import static com.pin.recommend.CharacterDetailActivity.INTENT_CHARACTER;

public class CharacterListActivity extends AppCompatActivity {

    private ListView charactersListView;
    private CharactersAdapter charactersAdapter;

    private RecommendCharacterViewModel characterViewModel;
    private AccountViewModel accountViewModel;
    private EditStateViewModel editListViewModel;

    private AdMobAdaptiveBannerManager adMobManager;
    private ViewGroup adViewContainer;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

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
        editListViewModel = new ViewModelProvider(this).get(EditStateViewModel.class);

        charactersAdapter = new CharactersAdapter(this, characterViewModel);

        charactersListView = findViewById(R.id.characters_listview);
        FloatingActionButton fab = findViewById(R.id.fab);

        charactersListView.setAdapter(charactersAdapter);
        charactersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CharacterListActivity.this, CharacterDetailActivity.class);
                intent.putExtra(INTENT_CHARACTER, charactersAdapter.getItem(position));
                startActivity(intent);
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
        final LiveData<List<RecommendCharacter>> characters = characterViewModel.getCharacters(accountLiveData);
        characters.observe(this, new Observer<List<RecommendCharacter>>() {
            @Override
            public void onChanged(List<RecommendCharacter> recommendCharacters) {
                charactersAdapter.setList(recommendCharacters);
            }
        });

        editListViewModel.getEditMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                charactersAdapter.setEditMode(aBoolean);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CharacterListActivity.this, CreateCharacterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        adMobManager.checkAndLoad();
    }

    private void initializeToolbar(Account account){
        toolbar.setTitle("推しリスト");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general, menu);

        final MenuItem editMode = menu.findItem(R.id.edit_mode);
        editListViewModel.getEditMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mode) {
                if(mode) {
                    editMode.setTitle("完了");
                }else{
                    editMode.setTitle("編集");
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_mode:
                if(editListViewModel.getEditMode().getValue()) {
                    editListViewModel.setEditMode(false);
                }else{
                    editListViewModel.setEditMode(true);
                }
                return true;
            case R.id.setting:
                startActivity(new Intent(this, GlobalSettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
