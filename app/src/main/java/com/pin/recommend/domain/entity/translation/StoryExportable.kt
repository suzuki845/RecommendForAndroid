package com.pin.recommend.domain.entity.translation

import com.pin.recommend.domain.entity.Story
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