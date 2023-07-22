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
class CustomAnniversary (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var characterId: Long,
    var date: Date,
    var uuid: String,
    var name: String,
    var topText: String?,
    var bottomText: String?,
    ){

    fun isAnniversary(current: Date): Boolean{
        val cc = Calendar.getInstance().apply { time = current }
        val ac = Calendar.getInstance().apply { time = date }

        return cc.get(Calendar.MONTH) == ac.get(Calendar.MONTH)
                && cc.get(Calendar.DAY_OF_MONTH) == ac.get(Calendar.DAY_OF_MONTH)
    }

    fun getElapsedDate(isZeroDayStart: Boolean) : Calendar{
        val anniversary = Calendar.getInstance()
        anniversary.time = TimeUtil.resetDate(date)
        if (!isZeroDayStart) {
            anniversary.add(Calendar.DAY_OF_MONTH, -1)
        }
        return anniversary
    }

    fun getRemainingTime(currentDate: Date, isZeroDayStart: Boolean) : Long?{
        val remaining = Calendar.getInstance()
        remaining.time = currentDate
        TimeUtil.resetTime(remaining)

        val anniversary = getElapsedDate(isZeroDayStart)
        val diff = TimeUtil.getDiffDays(anniversary, remaining)

        if(diff < 0){
            return null
        }
        return diff
    }

}