package com.pin.recommend.domain.entity

import android.content.Context
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.google.gson.Gson

data class CharacterWithAnniversaries(
    @Embedded val character: RecommendCharacter,
    @Relation(
        parentColumn = "id",
        entityColumn = "characterId"
    )
    val anniversaries: List<CustomAnniversary>
) {
    @Ignore
    val id = character.id

    fun anniversaries(): List<AnniversaryInterface> {
        val list = mutableListOf<AnniversaryInterface>()
        list.add(SystemDefinedAnniversaries(character).apply { initialize() })
        list.addAll(anniversaries.map { it.toUserDefinedAnniversary(character.isZeroDayStart) })
        return list
    }

    fun appearance(context: Context): Appearance {
        return character.appearance(context)
    }

    fun serializableAppearance(): SerializableAppearance {
        return character.serializableAppearance()
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): CharacterWithAnniversaries {
            return Gson().fromJson(json, CharacterWithAnniversaries::class.java)
        }
    }
}