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

    @Query("DELETE FROM Story")
    public void deleteAll();

    @Query("SELECT * FROM Story")
    public List<Story> findByAll();

    @Query("SELECT * FROM Story WHERE id = :id")
    public Story findById(long id);

    @Query("SELECT * FROM Story WHERE id = :id")
    public LiveData<Story> findByTrackedId(long id);

    @Query("SELECT * FROM Story WHERE characterId = :characterId")
    public List<Story> findByCharacterId(long characterId);

    @Query("SELECT * FROM Story WHERE characterId = :characterId ORDER BY created DESC")
    public LiveData<List<Story>> findByTrackedCharacterIdOrderDesc(long characterId);

    @Query("SELECT * FROM Story WHERE characterId = :characterId ORDER BY created ASC")
    public LiveData<List<Story>> findByTrackedCharacterIdOrderAsc(long characterId);

    @Query("SELECT * FROM Story WHERE characterId = :characterId " +
            "ORDER BY CASE WHEN :isAsc = 1 THEN created END ASC, " +
            "CASE WHEN :isAsc = 0 THEN created END DESC")
    public LiveData<List<Story>> findByTrackedCharacterIdOrderByCreated(long characterId, boolean isAsc);

}
