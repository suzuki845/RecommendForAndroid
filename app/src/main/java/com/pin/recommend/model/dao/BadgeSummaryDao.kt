package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pin.recommend.model.entity.BadgeSummary

@Dao
interface BadgeSummaryDao {
    @Query("SELECT * FROM BadgeSummary WHERE characterId = :characterId")
    fun watchByCharacterIdBadgeSummary(characterId: Long): LiveData<BadgeSummary?>

    @Query("SELECT * FROM BadgeSummary WHERE characterId = :characterId")
    fun findByCharacterIdBadgeSummary(characterId: Long): BadgeSummary?

    @Insert
    fun insertBadgeSummary(badge: BadgeSummary): Long

    @Update
    fun updateBadgeSummary(badge: BadgeSummary): Int

}