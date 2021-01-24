package com.pin.recommend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import android.content.Intent;
import android.os.Bundle;

import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.model.dao.RecommendCharacterDao;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.viewmodel.AccountViewModel;

import java.util.ArrayList;

import static com.pin.recommend.CharacterDetailActivity.INTENT_CHARACTER;


public class MainActivity extends AppCompatActivity {


    public static final String INTENT_ACCOUNT = "com.pin.recommend.MainActivity.INTENT_ACCOUNT";

    private AccountViewModel accountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent characterListIntent = new Intent(this, CharacterListActivity.class);
        final ArrayList<Intent> intents = new ArrayList<>();
        intents.add(characterListIntent);

        accountViewModel = MyApplication.getAccountViewModel(this);
        LiveData<RecommendCharacter> fixedCharacter = Transformations.switchMap(accountViewModel.getAccount(), new Function<Account, LiveData<RecommendCharacter>>() {
            @Override
            public LiveData<RecommendCharacter> apply(Account input) {
                RecommendCharacterDao characterDao = AppDatabase.getDatabase(MainActivity.this)
                        .recommendCharacterDao();
                Long fixedCharacterId = -1L;
                if(input != null) {
                    fixedCharacterId = input.fixedCharacterId;
                }
                if(fixedCharacterId == null){
                    fixedCharacterId = -1L;
                }

                return characterDao.findTrackedById(fixedCharacterId);
            }
        });
        fixedCharacter.observe(this, new Observer<RecommendCharacter>() {
            @Override
            public void onChanged(RecommendCharacter character) {
                if(character != null){
                    Intent characterDetailIntent = new Intent(MainActivity.this, CharacterDetailActivity.class);
                    characterDetailIntent.putExtra(INTENT_CHARACTER, character);
                    intents.add(characterDetailIntent);
                }
                startActivities(intents.toArray(new Intent[]{}));
                finish();
            }
        });
    }




}
