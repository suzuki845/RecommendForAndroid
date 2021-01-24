package com.pin.recommend.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pin.recommend.model.entity.Story;

import java.util.List;

@Dao
public interface StoryDao {

    @Insert
    public long insertStory(Story story);

    @Update
    public int updateStory(Story story);

    @Delete
    public void deleteStory(Story story);

    @Query("SELECT * FROM Story WHERE id = :id")
    public Story findById(long id);

    @Query("SELECT * FROM Story WHERE id = :id")
    public LiveData<Story> findByTrackedId(long id);

    @Query("SELECT * FROM Story WHERE characterId = :characterId")
    public List<Story> findByCharacterId(long characterId);

    @Query("SELECT * FROM Story WHERE characterId = :characterId")
    public LiveData<List<Story>> findByTrackedCharacterId(long characterId);


}
