package com.pin.recommend.domain.model

import android.content.Context
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.Event
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

enum class EventEditorAction {
    Init,
    Save
}

enum class EventEditorStatus {
    Processing,
    Success,
    Failure
}

data class EventEditorState(
    val action: EventEditorAction = EventEditorAction.Init,
    val status: EventEditorStatus = EventEditorStatus.Processing,
    val id: Long = 0,
    val characterId: Long? = null,
    val date: Date = Date(),
    val title: String = "",
    val memo: String = "",
    val errorMessage: String? = null
)

class EventEditor(context: Context) {

    private val db = AppDatabase.getDatabase(context)

    private val _state = MutableStateFlow(EventEditorState())

    val state = _state

    fun setEntity(e: Event?) {
        _state.value = _state.value.copy(
            id = e?.id ?: 0,
            characterId = e?.characterId,
            date = e?.date ?: Date(),
            title = e?.title ?: "",
            memo = e?.memo ?: ""
        )
    }

    fun setId(id: Long) {
        _state.value = _state.value.copy(id = id)
    }

    fun setCharacterId(characterId: Long) {
        _state.value = _state.value.copy(characterId = characterId)
    }

    fun setDate(date: Date) {
        _state.value = _state.value.copy(date = date)
    }

    fun setTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun setMemo(memo: String) {
        _state.value = _state.value.copy(memo = memo)
    }

    fun resetError() {
        _state.value = state.value.copy(errorMessage = null)
    }

    fun save() {
        try {
            _state.value = state.value.copy(
                action = EventEditorAction.Save,
                status = EventEditorStatus.Processing,
            )

            val s = _state.value
            var saveId = s.id
            val e = Event(
                id = saveId,
                characterId = s.characterId ?: throw RuntimeException("characterId is null"),
                date = s.date,
                title = s.title,
                memo = s.memo
            )

            if (saveId == 0L) {
                saveId = db.eventDao().insertEvent(e)
            } else {
                db.eventDao().updateEvent(e)
            }

            _state.value = state.value.copy(
                id = saveId,
                action = EventEditorAction.Save,
                status = EventEditorStatus.Success,
            )
        } catch (e: Exception) {
            _state.value = state.value.copy(
                action = EventEditorAction.Save,
                status = EventEditorStatus.Failure,
                errorMessage = e.message
            )
        }
    }
}