package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pin.recommend.model.entity.Badge

@Dao
interface BadgeDao {
    @Query("SELECT * FROM Badge WHERE characterId = :characterId")
    fun watchByCharacterIdBadge(characterId: Long): LiveData<List<Badge>>

    @Query("SELECT * FROM Badge WHERE characterId = :characterId")
    fun findByCharacterIdBadge(characterId: Long): List<Badge>

    @Insert
    fun insertBadge(badge: Badge): Long
}