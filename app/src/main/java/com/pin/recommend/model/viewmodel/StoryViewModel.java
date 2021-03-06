package com.pin.recommend.model.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.model.dao.StoryDao;
import com.pin.recommend.model.dao.StoryPictureDao;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;

import java.util.List;

public class StoryViewModel extends AndroidViewModel {

    private StoryDao storyDao;
    private StoryPictureDao storyPictureDao;

    private MutableLiveData<LiveData<List<Story>>> storiesLiveData = new MutableLiveData();

    public StoryViewModel(Application application){
        super(application);
        storyDao = AppDatabase.getDatabase(application).storyDao();
        storyPictureDao = AppDatabase.getDatabase(application).storyPictureDao();
    }

    public MutableLiveData<LiveData<List<Story>>> getStories(){
        return storiesLiveData;
    }

    public void findByTrackedCharacterIdOrderDesc(Long characterId){
        storiesLiveData.postValue(storyDao.findByTrackedCharacterIdOrderDesc(characterId));
    }

    public void findByTrackedCharacterIdOrderAsc(Long characterId){
        storiesLiveData.postValue(storyDao.findByTrackedCharacterIdOrderAsc(characterId));
    }

    public LiveData<List<Story>> findByTrackedCharacterIdOrderByCreated(Long characterId, boolean isAsc){
        return storyDao.findByTrackedCharacterIdOrderByCreated(characterId, isAsc);
    }

    public LiveData<Story> findByTrackedId(long id){
        return storyDao.findByTrackedId(id);
    }

    public void insertStory(final Story story){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyDao.insertStory(story);
            }
        });
    }

    public interface WithSavePicture {
        void onSave(long storyId);
    }

    public void insertStoryWithPicture(final Story story, final WithSavePicture savePicture){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                long insertId = storyDao.insertStory(story);
                savePicture.onSave(insertId);
            }
        });
    }

    public void update(final Story story){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyDao.updateStory(story);
            }
        });
    }

    public void updateWithPicture(final Story story, final WithSavePicture savePicture){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyDao.updateStory(story);
                savePicture.onSave(story.id);
            }
        });
    }

    public void deleteStory(final Story story){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                List<StoryPicture> storyPictures = storyPictureDao.findByStoryId(story.id);
                for(StoryPicture storyPicture : storyPictures){
                    storyPicture.deleteImage(getApplication());
                }
                storyDao.deleteStory(story);
            }
        });
    }




}
