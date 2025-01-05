package com.pin.recommend.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pin.recommend.domain.entity.Story
import com.pin.recommend.domain.model.CharacterDetails
import com.pin.recommend.domain.model.CharacterPinningManager

class CharacterDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDetails: CharacterDetails
    private val accountModel = CharacterPinningManager(application)

    init {
        characterDetails = CharacterDetails(application, accountModel)
        characterDetails.initialize()
    }

    val state = characterDetails.state

    val cwa = characterDetails.cwa

    val character = characterDetails.character

    val stories = characterDetails.stories

    val id = characterDetails.id

    val account = accountModel.entity

    val editModeStories = MutableLiveData<Boolean>()

    fun deleteStory(story: Story) {
        characterDetails.deleteStory(story)
    }

    fun changeAnniversary() {
        characterDetails.changeDisplayOnHomeAnniversary()
    }

    fun pinning() {
        characterDetails.pinning()
    }

    fun unpinning() {
        characterDetails.unpinning()
    }

    fun updateStorySortOrder(order: Int) {
        characterDetails.updateStorySortOrder(order)
    }

}