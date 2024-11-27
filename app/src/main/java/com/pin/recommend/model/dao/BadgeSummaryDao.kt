package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.pin.recommend.model.entity.BadgeSummary

@Dao
interface BadgeSummaryDao {
    @Query("SELECT * FROM BadgeSummary WHERE characterId = :characterId")
    fun watchByCharacterIdBadgeSummary(characterId: Long): LiveData<BadgeSummary?>
}