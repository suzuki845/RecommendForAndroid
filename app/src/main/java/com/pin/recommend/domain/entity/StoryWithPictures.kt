package com.pin.recommend.domain.entity
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.google.gson.Gson

data class StoryWithPictures(
    @Embedded val story: Story,
    @Relation(
        parentColumn = "id",
        entityColumn = "storyId"
    )
    val pictures: List<StoryPicture>
){
    @Ignore
    val id = story.id

    fun toJson(): String {
        val json = Gson().toJson(this)
        println("test!!!toJson ${json}")
        return json
    }

    companion object {
        fun fromJson(json: String): StoryWithPictures {
            val entity = Gson().fromJson(json, StoryWithPictures::class.java)
            println("test!!!fromJson ${entity}")
            return entity
        }
    }

}