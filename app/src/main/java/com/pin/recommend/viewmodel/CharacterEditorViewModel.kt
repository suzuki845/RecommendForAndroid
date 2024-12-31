package com.pin.recommend.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.pin.recommend.model.CharacterEditState
import com.pin.recommend.model.CharacterEditor
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.model.entity.CustomAnniversary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.Date

data class CharacterEditorViewModelState(
    private val modelState: CharacterEditState = CharacterEditState(),
    val isDeleteModeAnniversary: Boolean = false,
) {
    val status = modelState.status
    val action = modelState.action
    val id = modelState.id
    val name = modelState.name
    val created = modelState.created
    val iconImage = modelState.iconImage
    val beforeIconImageUri = modelState.beforeIconImageUri
    val backgroundImage = modelState.backgroundImage
    val backgroundImageOpacity = modelState.backgroundImageOpacity
    val beforeBackgroundImageUri = modelState.beforeBackgroundImageUri
    val backgroundColor = modelState.backgroundColor
    val homeTextColor = modelState.homeTextColor
    val homeTextShadowColor = modelState.homeTextShadowColor
    val aboveText = modelState.aboveText
    val belowText = modelState.belowText
    val isZeroDayStart = modelState.isZeroDayStart
    val elapsedDateFormat = modelState.elapsedDateFormat
    val fontFamily = modelState.fontFamily
    val anniversaries = modelState.anniversaries
    val errorMessage = modelState.errorMessage
    val isVisibleBackgroundImageOpacityView: Int
        get() =
            if (backgroundImageOpacity != 1f) View.VISIBLE else View.GONE

    val backgroundColorToBitmap = modelState.backgroundColorToBitmap

    val homeTextColorToBitmap = modelState.homeTextColorToBitmap

    val homeTextShadowColorToBitmap = modelState.homeTextShadowColorToBitmap

    val typeFace: (Context) -> Typeface? = modelState::typeface

}


class CharacterEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val model = CharacterEditor(application)

    private val _application = application

    private val isDeleteModeAnniversary = MutableStateFlow(false)

    val state = model.state.combine(isDeleteModeAnniversary) { v1, v2 ->
        CharacterEditorViewModelState(v1).copy(isDeleteModeAnniversary = v2)
    }

    val setName: (String) -> Unit = model::setName

    val setCreated: (Date) -> Unit = model::setCreated

    val setIconImage: (Bitmap?) -> Unit = model::setIconImage

    val setBackgroundImage: (Bitmap?) -> Unit = model::setBackgroundImage

    val setBackgroundColor: (Int) -> Unit = model::setBackgroundColor

    val setHomeTextColor: (Int) -> Unit = model::setHomeTextColor

    val setHomeTextShadowColor: (Int) -> Unit = model::setHomeTextShadowColor

    val setAboveText: (String) -> Unit = model::setAboveText

    val setBelowText: (String) -> Unit = model::setBelowText

    val setIsZeroDayStart: (Boolean) -> Unit = model::setIsZeroDayStart

    val setElapsedDateFormat: (Int) -> Unit = model::setElapsedDateFormat

    val setFontFamily: (String) -> Unit = model::setFontFamily

    val setBackgroundImageOpacity: (Float) -> Unit = model::setBackgroundImageOpacity

    val addAnniversary: (CustomAnniversary.Draft) -> Unit = model::addAnniversary

    val replaceAnniversary: (CustomAnniversary.Draft) -> Unit = model::replaceAnniversary

    val removeAnniversary: (Int) -> Unit = model::removeAnniversary

    val resetError: () -> Unit = model::resetError

    val setEntityById: (Long) -> Unit = model::setEntityById

    val setEntity: (CharacterWithAnniversaries?) -> Unit =
        model::setEntity

    fun setIsDeleteModeAnniversary(v: Boolean) {
        isDeleteModeAnniversary.value = v
    }

    fun save() {
        model.save()
        val updateWidgetRequest =
            Intent("android.appwidget.action.APPWIDGET_UPDATE").setClassName(/* TODO: provide the application ID. For example: */
                _application.packageName,
                "com.pin.recommend.widget.ContentWidgetProvider"
            )
        _application.sendBroadcast(updateWidgetRequest)
    }

}