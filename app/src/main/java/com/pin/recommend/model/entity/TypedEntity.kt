package com.pin.recommend.model.entity

import com.google.gson.Gson

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
) {

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): TypedEntity {
            return Gson().fromJson(json, TypedEntity::class.java)
        }
    }

}