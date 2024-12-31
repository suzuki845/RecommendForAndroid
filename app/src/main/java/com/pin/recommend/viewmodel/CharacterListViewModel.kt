package com.pin.recommend.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.util.combine2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CharacterListViewState
    (
    val characters: List<RecommendCharacter> = listOf(),
    val deleteMode: Boolean = false,
    val errorMessage: String? = null,
)

class CharacterListViewModel(application: Application) : AndroidViewModel(application) {
    private val characterDao: RecommendCharacterDao =
        AppDatabase.getDatabase(application).recommendCharacterDao()

    private val _application = application

    val deleteMode = MutableStateFlow(false)

    private val _state = MutableStateFlow(CharacterListViewState())

    val state: StateFlow<CharacterListViewState> = _state

    fun subscribe(owner: LifecycleOwner) {
        combine2(characterDao.watch(), deleteMode.asLiveData(), { a, b ->
            CharacterListViewState(
                characters = a ?: listOf(),
                deleteMode = b ?: false,
            )
        }).observe(owner) {
            _state.value = it
        }
    }

    fun delete(character: RecommendCharacter) {
        try {
            AppDatabase.getDatabase(getApplication()).characterDeleteLogic()
                .invoke(character, getApplication())
            val updateWidgetRequest =
                Intent("android.appwidget.action.APPWIDGET_UPDATE").setClassName(/* TODO: provide the application ID. For example: */
                    _application.packageName,
                    "com.pin.recommend.widget.ContentWidgetProvider"
                )
            _application.sendBroadcast(updateWidgetRequest)
        } catch (e: Exception) {
            _state.value = CharacterListViewState(
                characters = state.value.characters,
                deleteMode = state.value.deleteMode,
                errorMessage = e.message
            )
        }
    }

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

}