package com.pin.recommend.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    indices = [Index(name = "badgeCharacterId", value = ["characterId"])],
    foreignKeys = [
        ForeignKey(
            entity = RecommendCharacter::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
class Badge(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var characterId: Long,
    val uuid: String,
    var createdAt: Date,
    var updatedAt: Date
)