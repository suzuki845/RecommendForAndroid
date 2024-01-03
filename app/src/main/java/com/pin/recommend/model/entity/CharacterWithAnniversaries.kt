package com.pin.recommend.model.entity

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.google.gson.Gson
import com.pin.recommend.CharacterDetailActivity

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
        val typeface = character.fontFamily?.let {
            if (it == null) return@let null
            if (it == "Default") return@let null
            if (it == "default") return@let null
            if (it == "デフォルト") return@let null
            Typeface.createFromAsset(context.assets, "fonts/$it.ttf")
        }

        return Appearance(
            character.getIconImage(context, 500, 500),
            character.getBackgroundBitmap(context, 1000, 2000),
            character.backgroundColor ?: Color.parseColor("#ffffff"),
            character.homeTextColor ?: Color.parseColor("#444444"),
            character.homeTextShadowColor,
            character.elapsedDateFormat,
            typeface,
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