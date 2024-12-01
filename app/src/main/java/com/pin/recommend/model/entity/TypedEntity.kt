package com.pin.recommend.model.entity

class TypedEntity(
    val id: ContentId = ContentId.getEmpty(),
    val type: String,
    val name: String = "",
    val topText: String = "",
    val bottomText: String = "",
    val elapsedDays: Long = 0,
    val remainingDays: Long = 0,
    val message: String = "",
    val isAnniversary: Boolean = false,
    val badgeSummary: Int = 0,
)