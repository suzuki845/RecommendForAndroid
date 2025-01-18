package com.pin.recommend.ui.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.model.EventEditor
import com.pin.recommend.domain.model.EventEditorState
import kotlinx.coroutines.flow.map
import java.util.Date

data class EventEditorViewModelState(private val model: EventEditorState = EventEditorState()) {
    val action = model.action
    val status = model.status
    val id = model.id
    val characterId = model.characterId
    val date = model.date
    val title = model.title
    val memo = model.memo
    val errorMessage = model.errorMessage
}

class EventEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val editor = EventEditor(application)
    val state = editor.state.map {
        EventEditorViewModelState(it)
    }

    fun setEntity(e: Event?) {
        editor.setEntity(e)
    }

    fun setId(id: Long) {
        editor.setId(id)
    }

    fun setCharacterId(characterId: Long) {
        editor.setCharacterId(characterId)
    }

    fun setDate(date: Date) {
        editor.setDate(date)
    }

    fun setTitle(title: String) {
        editor.setTitle(title)
    }

    fun setMemo(memo: String) {
        editor.setMemo(memo)
    }

    fun resetError() {
        editor.resetError()
    }

    fun save() {
        editor.save()
    }
}