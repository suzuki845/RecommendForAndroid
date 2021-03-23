package com.pin.recommend.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.model.dao.RecommendCharacterDao;
import com.pin.recommend.model.dao.StoryDao;
import com.pin.recommend.model.dao.StoryPictureDao;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;

import java.util.List;

public class RecommendCharacterViewModel extends AndroidViewModel {

    private RecommendCharacterDao characterDao;
    private StoryDao storyDao;
    private StoryPictureDao storyPictureDao;

    private LiveData<List<RecommendCharacter>> characters;

    private LiveData<RecommendCharacter> character;

    public RecommendCharacterViewModel(@NonNull Application application) {
        super(application);
        characterDao = AppDatabase.getDatabase(application).recommendCharacterDao();
        storyDao = AppDatabase.getDatabase(application).storyDao();
        storyPictureDao = AppDatabase.getDatabase(application).storyPictureDao();
    }

    public LiveData<List<RecommendCharacter>> getCharacters(long accountId){
        if(characters == null) {
            characters = characterDao.findByAccountId(accountId);
        }
        return characters;
    }

    public LiveData<List<RecommendCharacter>> getCharacters(LiveData<Account> accountLiveData) {
        return Transformations.switchMap(accountLiveData, new Function<Account, LiveData<List<RecommendCharacter>>>() {
            @Override
            public LiveData<List<RecommendCharacter>> apply(Account input) {
                return characterDao.findByAccountId(input.id);
            }
        });
    }

    public LiveData<RecommendCharacter> getCharacter(Long characterId){
        if(character == null){
            character = characterDao.findTrackedById(characterId);
        }
        return character;
    }

    public void insert(final RecommendCharacter character){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                characterDao.insertCharacter(character);
            }
        });
    }

    public void update(final RecommendCharacter character){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                characterDao.updateCharacter(character);
            }
        });
    }

    public void delete(final RecommendCharacter character){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                character.deleteIconImage(getApplication());
                character.deleteBackgroundImage(getApplication());

                List<Story> stories = storyDao.findByCharacterId(character.id);
                for(Story story : stories){
                    List<StoryPicture> storyPictures = storyPictureDao.findByStoryId(story.id);
                    for(StoryPicture storyPicture : storyPictures){
                        storyPicture.deleteImage(getApplication());
                    }
                }

                characterDao.deleteCharacter(character);
            }
        });
    }

}
