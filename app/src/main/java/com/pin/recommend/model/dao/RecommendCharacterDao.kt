package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.model.entity.RecommendCharacter

@Dao
interface RecommendCharacterDao {
    @Insert
    fun insertCharacter(character: RecommendCharacter?): Long

    @Update
    fun updateCharacter(character: RecommendCharacter?): Int

    @Delete
    fun deleteCharacter(character: RecommendCharacter?)

    @Query("DELETE FROM RecommendCharacter")
    fun deleteAll()

    @Query("SELECT * FROM RecommendCharacter")
    fun findAll(): List<RecommendCharacter>

    @Query("SELECT * FROM RecommendCharacter WHERE id = :id")
    fun findById(id: Long): RecommendCharacter?

    @Query("SELECT * FROM RecommendCharacter WHERE id = :id")
    fun findTrackedById(id: Long): LiveData<RecommendCharacter?>

    @Query("SELECT * FROM RecommendCharacter WHERE accountId = :accountId")
    fun findByAccountId(accountId: Long): LiveData<List<RecommendCharacter>>
}