package com.pin.recommend.model

import androidx.lifecycle.MutableLiveData
import com.pin.recommend.model.entity.CustomAnniversary
import java.util.*

class AnniversaryEditModel {

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

    fun save(onComplete: (CustomAnniversary.Draft) -> Unit) {
        val characterId = characterId.value ?: throw Exception("foreign key is null")
        val uuid = uuid.value ?: UUID.randomUUID().toString()
        val date = date.value ?: throw Exception("date is null")
        val name = name.value ?: throw Exception("name is null")
        val topText = topText.value
        val bottomText = bottomText.value

        val drift = CustomAnniversary.Draft(
            characterId,
            date,
            uuid,
            name,
            topText,
            bottomText
        )

        onComplete(drift)

        this.characterId.value = null
        this.uuid.value = null
        this.date.value = null
        this.name.value = null
        this.topText.value = null
        this.bottomText.value = null
    }
}