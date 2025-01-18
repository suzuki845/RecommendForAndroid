package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.google.gson.Gson
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.AnniversaryData
import com.pin.recommend.domain.entity.Appearance
import com.pin.recommend.domain.entity.CharacterWithAnniversaries
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.entity.Story
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.util.combine2
import com.pin.recommend.util.combine5
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import java.util.LinkedList

enum class CharacterDetailsAction {
    Init,
    Pining,
    UnPining,
    DeleteStory,
    UpdateStoryOrder,
    DeletePayment,
    DeleteEvent
}

enum class CharacterDetailsStatus {
    Processing,
    Success,
    Failure
}

data class CharacterDetailsState(
    val action: CharacterDetailsAction = CharacterDetailsAction.Init,
    val status: CharacterDetailsStatus = CharacterDetailsStatus.Processing,
    val character: CharacterWithAnniversaries? = null,
    val fixedCharacterId: Long? = null,
    val characterName: String = "",
    val appearance: Appearance = Appearance(),
    val anniversaries: List<AnniversaryData> = listOf(),
    val storySortOrder: Int = 0,
    val stories: List<StoryWithPictures> = listOf(),
    val payments: MonthlyPayment = MonthlyPayment(),
    val events: SelectedMonthlyEvent = SelectedMonthlyEvent(),
    val errorMessage: String? = null,
) {

    val isPinning = fixedCharacterId == character?.id

    val currentAnniversary = anniversaries.firstOrNull() ?: AnniversaryData()

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): CharacterDetailsState {
            return Gson().fromJson(json, CharacterDetailsState::class.java)
        }
    }
}

class CharacterDetails(
    private val context: Context,
) {
    private val db = AppDatabase.getDatabase(context)

    private val pinningManager = CharacterPinningManager(context)

    private val eventModel = CharacterMonthlyEventModel(context)

    private val paymentModel = MonthlyPaymentModel(context)

    private val id = MutableLiveData<Long?>()

    private val character = combine2(id, pinningManager.account) { id, account ->
        return@combine2 db.recommendCharacterDao().watchByIdCharacterWithAnniversaries(
            (id ?: account?.fixedCharacterId) ?: -1
        )
    }.switchMap { it }

    private val stories = character.switchMap {
        if (it == null) return@switchMap MutableLiveData(listOf())
        return@switchMap db.storyDao()
            .watchByCharacterIdStoryWithPictures(it.id, it.character.storySortOrder == 1)
    }

    private val _dataStream = combine5(
        pinningManager.account,
        character,
        stories,
        eventModel.selectedMonthlyEvent,
        paymentModel.monthlyPayment
    ) { a, b, c, d, e ->
        val anniversaries = b?.anniversaries()?.map {
            AnniversaryData(
                id = it.getId(),
                name = it.getName(),
                topText = it.getTopText(),
                bottomText = it.getBottomText(),
                elapsedDays = it.getElapsedDays(Date()).let { d -> "${d}日" } ?: "",
                getRemainingDays = it.getRemainingDays(Date())?.let { d -> "${d}日" } ?: "",
                message = it.getMessage(Date()),
                isAnniversary = it.isAnniversary(Date())
            )
        } ?: listOf()

        return@combine5 CharacterDetailsState(
            character = b,
            fixedCharacterId = a?.fixedCharacterId,
            characterName = b?.character?.name ?: "",
            appearance = b?.appearance(context) ?: Appearance(),
            anniversaries = anniversaries,
            storySortOrder = b?.character?.storySortOrder ?: 0,
            stories = c ?: listOf(),
            events = d ?: SelectedMonthlyEvent(),
            payments = e ?: MonthlyPayment()
        )
    }

    private val _state = MutableStateFlow(CharacterDetailsState())

    val state: StateFlow<CharacterDetailsState> = _state

    fun observePinningCharacterId(owner: LifecycleOwner, callback: (Long?) -> Unit) {
        pinningManager.account.map {
            it.fixedCharacterId
        }.observe(owner) { id ->
            callback(id)
        }
    }

    fun setCharacterId(id: Long) {
        this.id.value = id
        eventModel.setCharacter(id)
        paymentModel.setCharacterId(id)
    }

    fun observe(owner: LifecycleOwner) {
        _dataStream.observe(owner) {
            _state.value = it
        }
    }

    fun changeAnniversary() {
        val current = LinkedList<AnniversaryData>()
        state.value.anniversaries.forEach {
            current.add(it)
        }

        current.addFirst(current.removeLast())
        _state.value = state.value.copy(anniversaries = current)
    }

    fun deleteStory(story: Story) {
        try {
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeleteStory,
                status = CharacterDetailsStatus.Processing,
            )
            AppDatabase.executor.execute {
                val storyPictures: List<StoryPicture> = db.storyPictureDao().findByStoryId(story.id)
                for (storyPicture in storyPictures) {
                    storyPicture.deleteImage(context)
                }
                db.storyDao().deleteStory(story)
            }
            _state.value = _state.value.copy(
                status = CharacterDetailsStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                status = CharacterDetailsStatus.Failure,
                errorMessage = e.message
            )
        }
    }

    fun deletePayment(payment: Payment) {
        try {
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeletePayment,
                status = CharacterDetailsStatus.Processing,
            )
            paymentModel.delete(payment)
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeletePayment,
                status = CharacterDetailsStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeletePayment,
                status = CharacterDetailsStatus.Failure,
                errorMessage = e.message
            )
        }
    }

    fun deleteEvent(event: Event) {
        try {
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeleteEvent,
                status = CharacterDetailsStatus.Processing,
            )
            eventModel.delete(event)
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeleteEvent,
                status = CharacterDetailsStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.DeleteEvent,
                status = CharacterDetailsStatus.Failure,
                errorMessage = e.message
            )
        }
    }

    fun setCurrentPaymentDate(date: Date) {
        paymentModel.setCurrentDate(date)
    }

    fun setCurrentEventDate(date: Date) {
        eventModel.setCurrentDate(date)
    }

    fun prevPaymentMonth() {
        paymentModel.prevMonth()
    }

    fun prevEventMonth() {
        eventModel.prevMonth()
    }

    fun nextPaymentMonth() {
        paymentModel.nextMonth()
    }

    fun nextEventMonth() {
        eventModel.nextMonth()
    }

    fun pinning() {
        _state.value = _state.value.copy(
            action = CharacterDetailsAction.Pining,
            status = CharacterDetailsStatus.Processing,
        )
        val id = id.value
        if (id != null) {
            pinningManager.pinning(id)
            _state.value = _state.value.copy(
                status = CharacterDetailsStatus.Success,
            )
        } else {
            _state.value = _state.value.copy(
                status = CharacterDetailsStatus.Failure,
            )
        }
    }

    fun unpinning() {
        _state.value = _state.value.copy(
            action = CharacterDetailsAction.UnPining,
            status = CharacterDetailsStatus.Processing,
        )
        pinningManager.unpinning()
        _state.value = _state.value.copy(
            status = CharacterDetailsStatus.Failure,
        )
    }

    fun updateStorySortOrder(order: Int) {
        try {
            _state.value = _state.value.copy(
                action = CharacterDetailsAction.UpdateStoryOrder,
                status = CharacterDetailsStatus.Processing,
            )
            val character = character.value?.character
            character?.storySortOrder = order
            AppDatabase.executor.execute {
                character?.let {
                    db.recommendCharacterDao().updateCharacter(
                        it
                    )
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                status = CharacterDetailsStatus.Failure,
                errorMessage = e.message
            )
        }
    }


}