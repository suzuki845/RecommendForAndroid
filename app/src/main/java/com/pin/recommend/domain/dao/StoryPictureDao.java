package com.pin.recommend.domain.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pin.recommend.domain.entity.StoryPicture;

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
    public int deleteStoryPicture(StoryPicture storyPicture);

    @Query("DELETE FROM StoryPicture")
    public void deleteAll();

    @Delete
    public void deleteAll(List<StoryPicture> storyPictures);

    @Query("SELECT * FROM StoryPicture")
    public List<StoryPicture> findByAll();

    @Query("SELECT * FROM StoryPicture WHERE id = :id")
    public StoryPicture findById(long id);

    @Query("SELECT * FROM StoryPicture WHERE storyId = :storyId")
    public LiveData<List<StoryPicture>> findByTrackedStoryId(long storyId);

    @Query("SELECT * FROM StoryPicture WHERE storyId = :storyId")
    public List<StoryPicture> findByStoryId(long storyId);


}
