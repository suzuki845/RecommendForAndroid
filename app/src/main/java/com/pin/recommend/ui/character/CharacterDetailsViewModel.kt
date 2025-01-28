package com.pin.recommend.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.entity.Story
import com.pin.recommend.domain.model.CharacterDetails
import com.pin.recommend.domain.model.CharacterDetailsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.Date


data class CharacterDetailsViewModelState(
    val state: CharacterDetailsState = CharacterDetailsState(),
    val isDeleteModeStories: Boolean = false,
    val isDeleteModePayments: Boolean = false,
    val isDeleteModeEvents: Boolean = false,
) {
    val action = state.action
    val status = state.status
    val character = state.character
    val fixedCharacterId = state.fixedCharacterId
    val isPinning = state.isPinning
    val characterName = state.characterName
    val appearance = state.appearance
    val anniversaries = state.anniversaries
    val currentAnniversary = state.currentAnniversary
    val storySortOrder = state.storySortOrder
    val stories = state.stories
    val payments = state.payments
    val events = state.events
    val errorMessage = state.errorMessage

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): CharacterDetailsViewModelState {
            return Gson().fromJson(json, CharacterDetailsViewModelState::class.java)
        }
    }

}

class CharacterDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val model = CharacterDetails(application)

    private val editModeStory = MutableStateFlow(false)

    private val editModePayment = MutableStateFlow(false)

    private val editModeEvent = MutableStateFlow(false)

    private val resultData = combine(
        model.state,
        editModeStory,
        editModePayment,
        editModeEvent,
    ) { a, b, c, d ->
        CharacterDetailsViewModelState(
            state = a,
            isDeleteModeStories = b,
            isDeleteModePayments = c,
            isDeleteModeEvents = d,
        )
    }

    private val _state = MutableStateFlow(CharacterDetailsViewModelState())

    val state: Flow<CharacterDetailsViewModelState> = _state

    fun observePinningCharacterId(owner: LifecycleOwner, callback: (Long?) -> Unit) {
        model.observePinningCharacterId(owner, callback)
    }

    fun setCharacterId(id: Long) {
        model.setCharacterId(id)
    }

    fun observe(owner: LifecycleOwner) {
        model.observe(owner)
        resultData.asLiveData().observe(owner) {
            _state.value = it
        }
    }

    fun changeAnniversary() {
        model.changeAnniversary()
    }

    fun pinning() {
        model.pinning()
    }

    fun unpinning() {
        model.unpinning()
    }

    fun togglePinning() {
        model.togglePinning()
    }

    fun setCurrentPaymentDate(date: Date) {
        model.setCurrentPaymentDate(date)
    }

    fun setCurrentEventDate(date: Date) {
        model.setCurrentEventDate(date)
    }

    fun prevPaymentMonth() {
        model.prevPaymentMonth()
    }

    fun nextPaymentMonth() {
        model.nextPaymentMonth()
    }

    fun prevEventMonth() {
        model.prevEventMonth()
    }

    fun nextEventMonth() {
        model.nextEventMonth()
    }

    fun deleteStory(story: Story) {
        model.deleteStory(story)
    }

    fun updateStorySortOrder(order: Int) {
        model.updateStorySortOrder(order)
    }

    fun deletePayment(payment: Payment) {
        model.deletePayment(payment)
    }

    fun deleteEvent(event: Event) {
        model.deleteEvent(event)
    }

    fun toggleEditModeStory() {
        editModeStory.value = editModeStory.value != true
    }

    fun toggleEditModePayment() {
        editModePayment.value = editModePayment.value != true
    }

    fun toggleEditModeEvent() {
        editModeEvent.value = editModeEvent.value != true
    }

    fun resetError() {
        model.resetError()
    }

}