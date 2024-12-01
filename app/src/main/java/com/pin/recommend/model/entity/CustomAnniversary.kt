package com.pin.recommend.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import java.util.Date


@Entity(
    indices = [Index(name = "customAnniversaryCharacterId", value = ["characterId"])],
    foreignKeys = [ForeignKey(
        entity = RecommendCharacter::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("characterId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
class CustomAnniversary(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var characterId: Long,
    val date: Date,
    val uuid: String,
    val name: String,
    val topText: String?,
    val bottomText: String?,
) {

    fun toUserDefinedAnniversary(isZeroDayStart: Boolean): UserDefinedAnniversary {
        return UserDefinedAnniversary(
            ContentId(characterId, id.toString()),
            name,
            date,
            isZeroDayStart,
            topText ?: "",
            bottomText ?: ""
        )
    }

    fun toDraft(): Draft {
        return Draft(id, characterId, date, uuid, name, topText, bottomText)
    }

    class Draft
        (
        var id: Long = 0,
        var characterId: Long,
        var date: Date,
        var uuid: String,
        var name: String,
        var topText: String?,
        var bottomText: String?,
    ) {

        fun toFinal(): CustomAnniversary {
            return CustomAnniversary(id, characterId, date, uuid, name, topText, bottomText)
        }

        fun toJson(): String {
            return Gson().toJson(this)
        }

        companion object {
            fun fromJson(json: String): Draft {
                return Gson().fromJson(json, Draft::class.java)
            }
        }
    }

}