package com.pin.recommend.domain.entity

import android.content.Context
import android.graphics.Color
import androidx.room.Embedded
import androidx.room.Ignore
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
    val badgeSummary: BadgeSummary?,
) {
    @Ignore
    var recentEvents: List<Event> = emptyList()

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
                    id = it.getId(),
                    type = "Anniversary",
                    name = it.getName(),
                    topText = it.getTopText(),
                    bottomText = it.getBottomText(),
                    elapsedDays = it.getElapsedDays(Date()),
                    remainingDays = it.getRemainingDays(Date()) ?: 0,
                    message = it.getMessage(Date()),
                    isAnniversary = it.isAnniversary(Date()),
                    badgeSummary = 0,
                    recentEvents = listOf()
                )
            }.toMutableList()
        entities.add(
            TypedEntity(
                id = ContentId(character.id, "/Bag"),
                type = "Bag",
                name = "痛バ",
                topText = "",
                bottomText = "",
                elapsedDays = 0,
                remainingDays = 0,
                message = "",
                isAnniversary = false,
                badgeSummary = badgeSummary?.amount ?: 0,
                recentEvents = listOf()
            )
        )
        entities.add(
            TypedEntity(
                id = ContentId(character.id, "/Event"),
                type = "Event",
                name = "イベント",
                topText = "",
                bottomText = "",
                elapsedDays = 0,
                remainingDays = 0,
                message = "",
                isAnniversary = false,
                badgeSummary = badgeSummary?.amount ?: 0,
                recentEvents = recentEvents
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