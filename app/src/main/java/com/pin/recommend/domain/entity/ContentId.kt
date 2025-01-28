package com.pin.recommend.domain.entity

data class ContentId(private val characterId: Long, private val contentId: String) {
    fun getId(): String {
        return "characters/$characterId/$contentId"
    }

    fun getCharacterId(): Long {
        return characterId
    }

    fun getContentId(): String {
        return contentId
    }

    companion object {
        fun getEmpty(): ContentId {
            return ContentId(-1, "null")
        }
    }
}
