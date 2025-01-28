package com.pin.recommend.domain.entity.translation

import com.pin.recommend.domain.entity.CustomAnniversary
import java.util.Date
import java.util.UUID

class CustomAnniversaryExportable {

    var date: Date? = null
    var uuid: String? = null
    var name: String? = null
    var topText: String? = null
    var bottomText: String? = null

    constructor(a: CustomAnniversary) {
        a.date?.let {
            date = it
        }
        a.uuid?.let {
            uuid = it
        }
        a.name?.let {
            name = it
        }
        a.topText?.let {
            topText = it
        }
        a.bottomText?.let {
            bottomText = it
        }
    }

    fun importable(): CustomAnniversary {
        return CustomAnniversary(
            id = 0,
            characterId = -1,
            date = date ?: Date(),
            uuid = uuid ?: UUID.randomUUID().toString(),
            name = name ?: "",
            topText = topText,
            bottomText = bottomText
        )
    }


}