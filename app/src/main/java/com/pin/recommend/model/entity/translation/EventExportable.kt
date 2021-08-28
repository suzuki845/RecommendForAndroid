package com.pin.recommend.model.entity.translation

import com.pin.recommend.model.entity.Event
import java.util.*

class EventExportable {
    var title: String? = null
    var body: String? = null
    var date: Date

    constructor(event: Event){
        date = event.date
        event.title?.let {
            title = it
        }
        event.memo?.let {
            body = it
        }
    }

    fun importable(): Event {
        return Event(
                id = 0,
                characterId = -1,
                title = title,
                memo = body,
                date = date
        )
    }
}