package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pin.recommend.model.entity.CustomAnniversary

interface CustomAnniversaryDao {

    @Insert
    fun insertPayment(payment: CustomAnniversary?): Long

    @Update
    fun updatePayment(payment: CustomAnniversary?): Int

    @Delete
    fun deletePayment(payment: CustomAnniversary?)

    @Query("DELETE FROM CustomAnniversary")
    fun deleteAll()

    @Query("SELECT * FROM CustomAnniversary")
    fun findAll(): List<CustomAnniversary>

    @Query("SELECT * FROM CustomAnniversary WHERE id = :id")
    fun findById(id: Long): CustomAnniversary?

    @Query("SELECT * FROM CustomAnniversary WHERE id = :id")
    fun findByTrackedId(id: Long): LiveData<CustomAnniversary?>

    @Query("SELECT * FROM Payment WHERE characterId = :characterId")
    fun findByCharacterId(characterId: Long): List<CustomAnniversary>

}