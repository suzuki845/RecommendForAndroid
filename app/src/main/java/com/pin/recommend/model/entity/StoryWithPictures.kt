package com.pin.recommend.model.entity
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

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
}