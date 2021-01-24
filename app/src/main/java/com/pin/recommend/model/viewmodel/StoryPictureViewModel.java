package com.pin.recommend.model.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.model.dao.StoryPictureDao;
import com.pin.recommend.model.entity.StoryPicture;

import java.util.List;

public class StoryPictureViewModel extends AndroidViewModel {

    private StoryPictureDao storyPictureDao;

    public StoryPictureViewModel(Application application){
        super(application);
        storyPictureDao = AppDatabase.getDatabase(application).storyPictureDao();
    }

    public LiveData<List<StoryPicture>> findByTrackedStoryId(long storyId){
        return storyPictureDao.findByTrackedStoryId(storyId);
    }

    public List<StoryPicture> findByStoryId(long storyId){
        return storyPictureDao.findByStoryId(storyId);
    }

    public StoryPicture findById(long id){
        return storyPictureDao.findById(id);
    }

    public void insert(final StoryPicture storyPicture){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyPictureDao.insertStoryPicture(storyPicture);
            }
        });
    }

    public void insertAll(final List<StoryPicture> storyPictures){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyPictureDao.insertAll(storyPictures);
            }
        });
    }

    public void delete(final StoryPicture storyPicture){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyPictureDao.deleteStoryPicture(storyPicture);
            }
        });
    }

    public void deleteAll(final List<StoryPicture> storyPictures){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                storyPictureDao.deleteAll(storyPictures);
            }
        });
    }


}
