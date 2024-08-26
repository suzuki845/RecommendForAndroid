package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.model.entity.CustomAnniversary

@Dao
interface CustomAnniversaryDao {

    @Insert
    fun insertAnniversary(anniversary: CustomAnniversary): Long

    @Update
    fun updateAnniversary(anniversary: CustomAnniversary): Int

    @Delete
    fun deleteAnniversary(anniversary: CustomAnniversary)

    @Query("DELETE FROM CustomAnniversary WHERE characterId = :characterId")
    fun deleteByCharacterId(characterId: Long)

    @Query("DELETE FROM CustomAnniversary")
    fun deleteAll()

    @Query("SELECT * FROM CustomAnniversary WHERE id = :id")
    fun findById(id: Long): CustomAnniversary?

    @Query("SELECT * FROM CustomAnniversary WHERE id = :id")
    fun watchById(id: Long): LiveData<CustomAnniversary?>

    @Query("SELECT * FROM CustomAnniversary WHERE characterId = :characterId")
    fun findByCharacterId(characterId: Long): List<CustomAnniversary>

    @Query("SELECT * FROM CustomAnniversary WHERE characterId = :characterId")
    fun watchByCharacterId(characterId: Long): LiveData<List<CustomAnniversary>>

}