package com.pin.recommend.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.pin.recommend.util.TimeUtil.Companion.resetTime
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = RecommendCharacter::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("characterId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
class Story {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(index = true)
    var characterId: Long = 0
    var comment: String? = null
    var created: Date? = null
    val formattedDate: String
        get() = FORMAT.format(created)
    @Ignore
    var pictures = listOf<StoryPicture>()

    fun getDiffDays(calendar1: Calendar): Long {
        resetTime(calendar1)
        val calendar2 = Calendar.getInstance()
        calendar2.time = created
        resetTime(calendar2)
        val diffTime = calendar1.timeInMillis - calendar2.timeInMillis
        return diffTime / MILLIS_OF_DAY
    }

    fun getShortComment(length: Int): String {
        var t = if (comment != null) comment!! else ""
        t = t.replace("\n", " ")
        return if (t.length >= length) {
            t.substring(0, length)
        } else t
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        @Ignore
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

        @Ignore
        val MILLIS_OF_DAY = 1000 * 60 * 60 * 24

        fun fromJson(json: String): Story {
            return Gson().fromJson(json, Story::class.java)
        }

    }

    class Draft (
        var id: Long = 0,
        var characterId: Long = 0,
        var comment: String? = null,
        var created: Date? = null
    ){

        val uuid = UUID.randomUUID()

        fun toFinal(): Story {
            return Story().apply {
                this.id = id
                this.characterId = characterId
                this.comment = comment
                this.created = created
            }
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