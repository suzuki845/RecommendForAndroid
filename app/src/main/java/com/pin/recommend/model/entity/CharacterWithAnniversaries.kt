package com.pin.recommend.model.entity

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class CharacterWithAnniversaries(
    @Embedded val character: RecommendCharacter,
    @Relation(
        parentColumn = "id",
        entityColumn = "characterId"
    )
    val anniversaries: List<CustomAnniversary>
){
    @Ignore
    val id = character.id
}