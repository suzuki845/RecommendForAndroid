package com.pin.recommend.domain.entity

import androidx.room.*
import java.util.*

@Entity(
    indices = [Index(name = "eventCharacterId", value = ["characterId"])],
    foreignKeys = [
            ForeignKey(
                    entity = RecommendCharacter::class,
                    parentColumns = ["id"],
                    childColumns = ["characterId"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE)
        ]
)
class Event(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        var characterId: Long,
        var title: String?,
        var memo: String?,
        var date: Date
){

    fun getTitleComment(length: Int): String? {
        var t = title ?: ""
        t = t.replace("\n", " ")
        return if (t.length >= length) {
            t.substring(0, length)
        } else t
    }

    fun getShortComment(length: Int): String? {
        var t = memo ?: ""
        t = t.replace("\n", " ")
        return if (t.length >= length) {
            t.substring(0, length)
        } else t
    }

}
