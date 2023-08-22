package com.pin.recommend.model.entity

import androidx.room.*
import com.pin.recommend.util.TimeUtil
import java.util.*


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
    var date: Date,
    var uuid: String,
    var name: String,
    var topText: String?,
    var bottomText: String?,
) {

    fun toDraft(): Draft {
        return Draft(characterId, date, uuid, name, topText, bottomText)
    }

    class Draft
        (
        var characterId: Long,
        var date: Date,
        var uuid: String,
        var name: String,
        var topText: String?,
        var bottomText: String?,
    )

}