package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.PaymentAndTag
import java.util.*

@Dao
interface EventDao {
    @Insert
    fun insertEvent(event: Event?): Long

    @Update
    fun updateEvent(event: Event?): Int

    @Delete
    fun deleteEvent(event: Event?)

    @Query("SELECT * FROM Event WHERE id = :id")
    fun findById(id: Long): Event?

    @Query("SELECT * FROM Event WHERE id = :id")
    fun findByTrackedId(id: Long): LiveData<Event?>

    @Query("SELECT * FROM Event WHERE characterId = :characterId")
    fun findByCharacterId(characterId: Long): List<Event>

    @Transaction
    @Query("SELECT * FROM Event WHERE characterId = :characterId")
    fun findByTrackedCharacterIdEvent(characterId: Long): LiveData<List<Event>>

    @Transaction
    @Query("SELECT * FROM Event WHERE characterId = :characterId AND (date >= :start AND date <= :end) ORDER BY date ASC")
    fun findByTrackedCharacterIdEventInDate(characterId: Long, start: Date, end: Date): LiveData<List<Event>>

}