package com.pin.recommend.model.entity

import java.util.Date

interface AnniversaryInterface {

    fun getId(): ContentId
    fun getName(): String
    fun getTopText(): String
    fun getBottomText(): String
    fun getRemainingDays(current: Date): Long?
    fun getElapsedDays(current: Date): Long
    fun isAnniversary(current: Date): Boolean
    fun getMessage(current: Date): String

    fun toTypedEntity(date: Date): TypedEntity {
        return TypedEntity(
            id = getId(),
            type = "Anniversary",
            name = getName(),
            topText = getTopText(),
            bottomText = getBottomText(),
            elapsedDays = getElapsedDays(date) ?: 0,
            remainingDays = getRemainingDays(date) ?: 0,
            message = getMessage(date),
            isAnniversary = isAnniversary(date),
            badgeSummary = 0
        )
    }
}

