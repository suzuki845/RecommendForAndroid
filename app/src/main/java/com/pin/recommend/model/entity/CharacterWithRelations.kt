package com.pin.recommend.model.entity

import android.content.Context
import android.graphics.Color
import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.Gson
import java.util.Date

class CharacterWithRelations(
    @Embedded val character: RecommendCharacter,
    @Relation(
        parentColumn = "id",
        entityColumn = "characterId"
    )
    val anniversaries: List<CustomAnniversary>,
    @Relation(
        parentColumn = "id",
        entityColumn = "characterId"
    )
    val badgeSummary: BadgeSummary,
) {
    fun anniversaries(): List<AnniversaryInterface> {
        val list = mutableListOf<AnniversaryInterface>()
        list.add(SystemDefinedAnniversaries(character).apply { initialize() })
        list.addAll(anniversaries.map { it.toUserDefinedAnniversary(character.isZeroDayStart) })
        return list
    }

    fun typedEntities(): List<TypedEntity> {
        val entities =
            anniversaries().map {
                TypedEntity(
                    id = it.getId().getId(),
                    type = "Anniversary",
                    name = it.getName(),
                    topText = it.getTopText(),
                    bottomText = it.getBottomText(),
                    elapsedDays = it.getElapsedDays(Date()),
                    remainingDays = it.getRemainingDays(Date()) ?: 0,
                    isAnniversary = it.isAnniversary(Date()),
                    badgeSummary = 0
                )
            }.toMutableList()
        entities.add(
            TypedEntity(
                id = "characters/${character.id}/badge",
                type = "Badge",
                name = "Badge",
                topText = "",
                bottomText = "",
                elapsedDays = 0,
                remainingDays = 0,
                isAnniversary = false,
                badgeSummary = badgeSummary.amount
            )
        )
        return entities
    }

    fun appearance(context: Context): Appearance {
        return Appearance(
            character.getIconImage(context, 500, 500),
            character.getBackgroundBitmap(context, 1000, 2000),
            character.backgroundColor ?: Color.parseColor("#77ffffff"),
            character.homeTextColor ?: Color.parseColor("#ff000000"),
            character.homeTextShadowColor ?: Color.parseColor("#00000000"),
            character.elapsedDateFormat,
            character.fontFamily,
            character.backgroundImageOpacity
        )
    }

    fun serializableAppearance(): SerializableAppearance {
        return SerializableAppearance(
            character.iconImageUri,
            character.backgroundImageUri,
            character.backgroundColor ?: Color.parseColor("#77ffffff"),
            character.homeTextColor ?: Color.parseColor("#ff000000"),
            character.homeTextShadowColor ?: Color.parseColor("#00000000"),
            character.elapsedDateFormat,
            character.fontFamily,
            character.backgroundImageOpacity
        )
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