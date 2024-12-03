package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.model.entity.CharacterWithRelations
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.RecommendCharacter

@Dao
interface RecommendCharacterDao {
    @Insert
    fun insertCharacter(character: RecommendCharacter): Long

    @Update
    fun updateCharacter(character: RecommendCharacter): Int

    @Delete
    fun deleteCharacter(character: RecommendCharacter)

    @Query("DELETE FROM RecommendCharacter")
    fun deleteAll()

    @Query("SELECT * FROM RecommendCharacter")
    fun findAll(): List<RecommendCharacter>

    @Query("SELECT * FROM RecommendCharacter")
    fun watch(): LiveData<List<RecommendCharacter>>

    @Query("SELECT * FROM RecommendCharacter WHERE id = :id")
    fun findById(id: Long): RecommendCharacter?

    @Query("SELECT * FROM RecommendCharacter WHERE id = :id")
    fun watchById(id: Long): LiveData<RecommendCharacter?>

    @Query("SELECT * FROM RecommendCharacter WHERE accountId = :accountId")
    fun watchByAccountId(accountId: Long): LiveData<List<RecommendCharacter>>

    @Transaction
    @Query(
        "SELECT * FROM RecommendCharacter"
    )
    fun watchCharacterWithAnniversaries(): LiveData<List<CharacterWithAnniversaries>>

    @Transaction
    @Query(
        "SELECT * FROM RecommendCharacter"
    )
    fun findCharacterWithAnniversaries(): List<CharacterWithAnniversaries>

    @Transaction
    @Query(
        "SELECT * FROM RecommendCharacter " +
                "WHERE id = :id"
    )
    fun watchByIdCharacterWithAnniversaries(id: Long): LiveData<CharacterWithAnniversaries?>

    @Transaction
    @Query(
        "SELECT * FROM RecommendCharacter " +
                "WHERE id = :id"
    )
    fun findByIdCharacterWithAnniversaries(id: Long): CharacterWithAnniversaries?


    @Transaction
    @Query(
        "SELECT * FROM RecommendCharacter"
    )
    fun findCharacterWithRelations(): List<CharacterWithRelations>

    @Transaction
    @Query(
        "SELECT * FROM RecommendCharacter " +
                "WHERE id = :id"
    )
    fun findByIdCharacterWithRelations(id: Long): CharacterWithRelations?

    @Query(
        """
        SELECT * FROM Event
        WHERE characterId = :characterId
        ORDER BY date ASC
        LIMIT 10
    """
    )
    fun findRecentEventsForCharacter(characterId: Long): List<Event>

    @Transaction
    fun findByIdCharacterWithRelationsAndRecentEvents(characterId: Long): CharacterWithRelations? {
        val characterWithRelations = findByIdCharacterWithRelations(characterId)
        val recentEvents = findRecentEventsForCharacter(characterId)
        characterWithRelations?.recentEvents = recentEvents
        return characterWithRelations
    }

    @Transaction
    fun findCharacterWithRelationsAndRecentEvents(): List<CharacterWithRelations> {
        val characterWithRelations = findCharacterWithRelations()
        return characterWithRelations.map {
            val recentEvents = findRecentEventsForCharacter(it.character.id)
            it.recentEvents = recentEvents
            it
        }
    }

}