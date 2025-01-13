package com.pin.recommend.domain.model

import com.pin.recommend.domain.entity.CustomAnniversary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import java.util.UUID

enum class AnniversaryEditorAction {
    Init,
    Create,
    Update,
}

enum class AnniversaryEditorStatus {
    Processing,
    Success,
    Failure,
}

data class AnniversaryEditorState(
    val action: AnniversaryEditorAction = AnniversaryEditorAction.Init,
    val status: AnniversaryEditorStatus = AnniversaryEditorStatus.Processing,
    val id: Long = 0L,
    val characterId: Long = -1L,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val date: Date = Date(),
    val topText: String = "",
    val bottomText: String = "",
    val errorMessage: String? = null,
) {
    fun toDraft(): CustomAnniversary.Draft {
        return CustomAnniversary.Draft(
            id,
            characterId,
            date,
            uuid,
            name,
            topText,
            bottomText
        )
    }
}

class AnniversaryEditor {

    private val _state = MutableStateFlow(AnniversaryEditorState())

    val state: StateFlow<AnniversaryEditorState> = _state

    fun setAction(a: AnniversaryEditorAction) {
        _state.value = _state.value.copy(
            action = a
        )
    }

    fun setEntity(e: CustomAnniversary.Draft? = null) {
        _state.value = AnniversaryEditorState(
            id = e?.id ?: 0,
            uuid = e?.uuid ?: UUID.randomUUID().toString(),
            characterId = e?.characterId ?: -1L,
            name = e?.name ?: "",
            date = e?.date ?: Date(),
            topText = e?.topText ?: "",
            bottomText = e?.bottomText ?: ""
        )
    }

    fun setCharacterId(v: Long) {
        _state.value = _state.value.copy(
            characterId = v
        )
    }

    fun setName(v: String) {
        _state.value = _state.value.copy(
            name = v
        )
    }

    fun setDate(v: Date) {
        _state.value = _state.value.copy(
            date = v
        )
    }

    fun setTopText(v: String) {
        _state.value = _state.value.copy(
            topText = v
        )
    }

    fun setBottomText(v: String) {
        _state.value = _state.value.copy(
            bottomText = v
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }

    fun done() {
        try {
            _state.value = _state.value.copy(
                status = AnniversaryEditorStatus.Processing
            )
            val s = _state.value
            if (s.characterId == -1L) {
                throw Exception("foreign key is null")
            }
            if (s.name.isBlank()) {
                throw Exception("記念日名がありません")
            }

            _state.value = _state.value.copy(
                status = AnniversaryEditorStatus.Success,
            )

        } catch (e: Exception) {
            _state.value = _state.value.copy(
                status = AnniversaryEditorStatus.Failure,
                errorMessage = e.message
            )
        }
    }
}