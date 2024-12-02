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

    fun toData(date: Date): AnniversaryData {
        return AnniversaryData(
            getId(),
            getName(),
            getTopText(),
            getBottomText(),
            getElapsedDays(date).let { d -> "${d}日" },
            getRemainingDays(date).let { d -> "${d}日" },
            getMessage(date),
            isAnniversary(date)
        )
    }

    fun toTypedEntity(date: Date): TypedEntity {
        return TypedEntity(
            getId(),
            "Anniversary",
            getName(),
            getTopText(),
            getBottomText(),
            getElapsedDays(date),
            getRemainingDays(date) ?: 0,
            getMessage(date),
            isAnniversary(date),
            0
        )
    }
}


data class AnniversaryData(
    val id: ContentId = ContentId.getEmpty(),
    val name: String = "",
    val topText: String = "",
    val bottomText: String = "",
    val elapsedDays: String = "",
    val getRemainingDays: String = "",
    val message: String = "",
    val isAnniversary: Boolean = false,
)