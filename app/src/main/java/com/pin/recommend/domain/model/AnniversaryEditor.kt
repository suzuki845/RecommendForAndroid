package com.pin.recommend.domain.model

import androidx.lifecycle.MutableLiveData
import com.pin.recommend.domain.entity.CustomAnniversary
import java.util.Date
import java.util.UUID

class AnniversaryEditor {

    val id = MutableLiveData<Long?>(null)
    val characterId = MutableLiveData<Long?>(null)
    val uuid = MutableLiveData<String?>(null)
    val name = MutableLiveData<String?>()
    val date = MutableLiveData(Date())
    val topText = MutableLiveData("")
    val bottomText = MutableLiveData("")

    fun initialize(e: CustomAnniversary? = null) {
        characterId.value = e?.characterId
        uuid.value = e?.uuid
        name.value = e?.name
        date.value = e?.date
        topText.value = e?.topText
        bottomText.value = e?.bottomText
    }

    fun initialize(e: CustomAnniversary.Draft? = null) {
        characterId.value = e?.characterId
        uuid.value = e?.uuid
        name.value = e?.name
        date.value = e?.date
        topText.value = e?.topText
        bottomText.value = e?.bottomText
    }

    fun save(onComplete: (CustomAnniversary.Draft) -> Unit, onError: (e: Exception) -> Unit) {
        try {
            val id = id.value ?: 0
            val characterId = characterId.value ?: throw Exception("foreign key is null")
            val uuid = uuid.value ?: UUID.randomUUID().toString()
            val date = date.value ?: throw Exception("date is null")
            val name = name.value ?: throw Exception("記念日名がありません. name is null")
            val topText = topText.value
            val bottomText = bottomText.value

            val drift = CustomAnniversary.Draft(
                id,
                characterId,
                date,
                uuid,
                name,
                topText,
                bottomText
            )

            onComplete(drift)

            this.id.value = null
            this.characterId.value = null
            this.uuid.value = null
            this.date.value = null
            this.name.value = null
            this.topText.value = null
            this.bottomText.value = null
        } catch (e: Exception) {
            onError(e)
            print("test!!" + e)
        }
    }
}