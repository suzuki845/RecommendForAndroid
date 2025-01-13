package com.pin.recommend.ui.anniversary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.domain.model.AnniversaryEditor
import com.pin.recommend.domain.model.AnniversaryEditorAction
import com.pin.recommend.domain.model.AnniversaryEditorState
import kotlinx.coroutines.flow.map
import java.util.Date

data class AnniversaryEditorViewModelState(val state: AnniversaryEditorState = AnniversaryEditorState()) {
    val action = state.action
    val status = state.status
    val id = state.id
    val characterId = state.characterId
    val uuid = state.uuid
    val name = state.name
    val date = state.date
    val topText = state.topText
    val bottomText = state.bottomText
    val errorMessage = state.errorMessage
    fun toDraft() = state.toDraft()
}

class AnniversaryEditorViewModel(application: Application) : AndroidViewModel(application) {

    val model = AnniversaryEditor()

    val state = model.state.map {
        AnniversaryEditorViewModelState(it)
    }

    fun setAction(a: AnniversaryEditorAction) {
        model.setAction(a)
    }

    fun setEntity(e: CustomAnniversary.Draft? = null) {
        model.setEntity(e)
    }

    fun setCharacterId(v: Long) {
        model.setCharacterId(v)
    }

    fun setName(v: String) {
        model.setName(v)
    }

    fun setDate(v: Date) {
        model.setDate(v)
    }

    fun setTopText(v: String) {
        model.setTopText(v)
    }

    fun setBottomText(v: String) {
        model.setBottomText(v)
    }

    fun resetError() {
        model.resetError()
    }

    fun done() {
        model.done()
    }

}