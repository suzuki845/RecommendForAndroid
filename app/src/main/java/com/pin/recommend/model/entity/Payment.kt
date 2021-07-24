package com.pin.recommend.model.entity

import androidx.room.*
import java.util.*

@Entity(
        foreignKeys = [
                ForeignKey(
                        entity = RecommendCharacter::class,
                        parentColumns = ["id"],
                        childColumns = ["characterId"],
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE)
        ]
)
class Payment(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        var characterId: Long,
        var paymentTagId: Long?,
        @ColumnInfo(defaultValue = "0")
        var type: Int,
        @ColumnInfo(defaultValue = "0.0")
        var amount: Double,
        var memo: String?,
        var createdAt: Date,
        var updatedAt: Date
){
        fun getShortComment(length: Int): String? {
                var t = memo ?: ""
                t = t.replace("\n", " ")
                return if (t.length >= length) {
                        t.substring(0, length)
                } else t
        }

        var toIntAmount get() = amount.toInt()
        set(value) {amount = value.toDouble()}

}

@Entity
class PaymentTag(
        @PrimaryKey(autoGenerate = true)
        var id: Long,
        @ColumnInfo(defaultValue = "0")
        var type: Int,
        var tagName: String,
        var createdAt: Date,
        var updatedAt: Date
) {
}

class PaymentAndTag(
        @Embedded
        var payment: Payment,
        @Relation(
                parentColumn = "paymentTagId",
                entityColumn = "id"
        )
        var tag: PaymentTag?
) {

}
