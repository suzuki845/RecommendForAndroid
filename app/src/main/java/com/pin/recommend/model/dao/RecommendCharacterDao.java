package com.pin.recommend.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.pin.recommend.model.entity.RecommendCharacter;

import java.util.List;

@Dao
public interface RecommendCharacterDao {

    @Insert
    public long insertCharacter(RecommendCharacter character);

    @Update
    public int updateCharacter(RecommendCharacter character);

    @Delete
    public void deleteCharacter(RecommendCharacter character);

    @Query("DELETE FROM RecommendCharacter")
    public void deleteAll();

    @Query("SELECT * FROM RecommendCharacter")
    List<RecommendCharacter> findAll();

    @Query("SELECT * FROM RecommendCharacter WHERE id = :id")
    public RecommendCharacter findById(long id);

    @Query("SELECT * FROM RecommendCharacter WHERE id = :id")
    public LiveData<RecommendCharacter> findTrackedById(long id);

    @Query("SELECT * FROM RecommendCharacter WHERE accountId = :accountId")
    public LiveData<List<RecommendCharacter>> findByAccountId(long accountId);



}
