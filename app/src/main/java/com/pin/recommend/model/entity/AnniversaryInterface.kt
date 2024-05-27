package com.pin.recommend.model.entity

import java.util.Date

interface AnniversaryInterface {

    fun getId(): AnniversaryId
    fun getName(): String
    fun getTopText(): String
    fun getBottomText(): String
    fun getRemainingDays(current: Date): Long?
    fun getElapsedDays(current: Date): Long
    fun isAnniversary(current: Date): Boolean
    fun getMessage(current: Date): String

    fun toData(date: Date): Anniversary {
        return Anniversary(
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
}

data class AnniversaryId(private val characterId: Long, private val anniversaryId: String) {
    fun getId(): String {
        return "anniversaries/characterId/$characterId/anniversaryId/$anniversaryId"
    }

    fun getCharacterId(): Long {
        return characterId
    }

    fun getAnniversaryId(): String {
        return anniversaryId
    }

    companion object {
        fun getEmpty(): AnniversaryId {
            return AnniversaryId(-1, "null")
        }
    }
}

data class Anniversary(
    val id: AnniversaryId = AnniversaryId.getEmpty(),
    val name: String = "",
    val topText: String = "",
    val bottomText: String = "",
    val elapsedDays: String = "",
    val getRemainingDays: String = "",
    val message: String = "",
    val isAnniversary: Boolean = false
)