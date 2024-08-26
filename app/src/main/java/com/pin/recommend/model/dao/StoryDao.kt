package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryWithPictures

@Dao
interface StoryDao {
    @Insert
    fun insertStory(story: Story): Long

    @Update
    fun updateStory(story: Story): Int

    @Delete
    fun deleteStory(story: Story)

    @Query("DELETE FROM Story")
    fun deleteAll()

    @Query("SELECT * FROM Story")
    fun findAll(): List<Story>

    @Query("SELECT * FROM Story WHERE id = :id")
    fun findById(id: Long): Story?

    @Query("SELECT * FROM Story WHERE id = :id")
    fun watchById(id: Long): LiveData<Story>

    @Query("SELECT * FROM Story WHERE characterId = :characterId")
    fun findByCharacterId(characterId: Long): List<Story>

    @Query("SELECT * FROM Story WHERE characterId = :characterId ORDER BY created DESC")
    fun watchByCharacterIdOrderDesc(characterId: Long): LiveData<List<Story>>

    @Query("SELECT * FROM Story WHERE characterId = :characterId ORDER BY created ASC")
    fun watchByCharacterIdOrderAsc(characterId: Long): LiveData<List<Story>>

    @Query(
        "SELECT * FROM Story WHERE characterId = :characterId " +
                "ORDER BY CASE WHEN :isAsc = 1 THEN created END ASC, " +
                "CASE WHEN :isAsc = 0 THEN created END DESC"
    )
    fun watchByCharacterIdOrderByCreated(
        characterId: Long,
        isAsc: Boolean
    ): LiveData<List<Story>>

    @Transaction
    @Query(
        "SELECT * FROM Story " +
                "WHERE characterId = :characterId " +
                "ORDER BY CASE WHEN :isAsc = 1 THEN created END ASC, " +
                "CASE WHEN :isAsc = 0 THEN created END DESC"
    )
    fun watchByCharacterIdStoryWithPictures(
        characterId: Long, isAsc: Boolean
    ): LiveData<List<StoryWithPictures>>

    @Transaction
    @Query(
        "SELECT * FROM Story " +
                "WHERE id = :id"
    )
    fun watchByIdStoryWithPictures(id: Long): LiveData<StoryWithPictures?>

    @Transaction
    @Query(
        "SELECT * FROM Story "
    )
    fun watchStoryWithPictures(): LiveData<List<StoryWithPictures>>

}