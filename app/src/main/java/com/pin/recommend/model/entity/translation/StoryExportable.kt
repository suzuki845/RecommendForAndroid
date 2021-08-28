package com.pin.recommend.model.entity.translation

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.pin.recommend.model.entity.Story
import com.pin.recommend.model.entity.StoryPicture
import java.util.*

class StoryExportable {
    var pictures: List<StoryPictureExportable> = mutableListOf()

    var comment: String? = null

    var created: Date? = null

    constructor(story: Story){
        comment = story.comment
        created = story.created
    }

    fun importable(): Story{
        val story = Story()
        story.comment = comment
        story.created = created
        story.pictures = pictures.map { it.importable() }
        return story
    }

}