package com.pin.recommend.domain.entity.translation

import com.pin.recommend.domain.entity.StoryPicture

class StoryPictureExportable {

    var src: String?

    constructor(picture: StoryPicture){
        src = picture.uri
    }

    fun importable(): StoryPicture {
        val picture = StoryPicture()
        picture.uri = src

        return picture
    }
}