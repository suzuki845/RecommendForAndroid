package com.pin.recommend.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;

import java.util.List;

@Dao
public interface StoryPictureDao {

    @Insert
    public long insertStoryPicture(StoryPicture storyPicture);

    @Delete
    public void insertAll(List<StoryPicture> storyPictures);

    @Update
    public int updateStoryPicture(StoryPicture storyPicture);

    @Delete
    public void deleteStoryPicture(StoryPicture storyPicture);

    @Delete
    public void deleteAll(List<StoryPicture> storyPictures);

    @Query("SELECT * FROM StoryPicture WHERE id = :id")
    public StoryPicture findById(long id);

    @Query("SELECT * FROM StoryPicture WHERE storyId = :storyId")
    public LiveData<List<StoryPicture>> findByTrackedStoryId(long storyId);

    @Query("SELECT * FROM StoryPicture WHERE storyId = :storyId")
    public List<StoryPicture> findByStoryId(long storyId);


}
