package com.pin.recommend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.model.AccountModel
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.entity.Story

class CharacterDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDetails: CharacterDetails
    private val accountModel = AccountModel(application)

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