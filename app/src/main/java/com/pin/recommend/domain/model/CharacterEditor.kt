package com.pin.recommend.domain.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.CharacterWithAnniversaries
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.domain.entity.RecommendCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

enum class CharacterEditAction {
    Init,
    AddAnniversary,
    ReplaceAnniversary,
    RemoveAnniversary,
    Save,
}

enum class CharacterEditStatus {
    Processing,
    Success,
    Failure
}

data class CharacterEditState(
    val status: CharacterEditStatus = CharacterEditStatus.Processing,
    val action: CharacterEditAction = CharacterEditAction.Init,
    val id: Long = 0,
    val name: String = "",
    val created: Date = Date(),
    val iconImage: Bitmap? = null,
    val beforeIconImageUri: String? = null,
    val backgroundImage: Bitmap? = null,
    val backgroundImageOpacity: Float = defaultBackgroundImageOpacity,
    val beforeBackgroundImageUri: String? = null,
    val backgroundColor: Int = defaultBackgroundColor,
    val homeTextColor: Int = defaultTextColor,
    val homeTextShadowColor: Int = defaultTextShadowColor,
    val aboveText: String = "を推して",
    val belowText: String = "になりました",
    val isZeroDayStart: Boolean = false,
    val elapsedDateFormat: Int = 0,
    val fontFamily: String = "デフォルト",
    val anniversaries: List<CustomAnniversary.Draft> = listOf(),
    val errorMessage: String? = null
) {
    companion object {
        val defaultBackgroundColor = Color.parseColor("#77ffffff")
        val defaultBackgroundImageOpacity = 1f
        val defaultTextColor = Color.parseColor("#ff000000")
        val defaultTextShadowColor = Color.parseColor("#00000000")
    }

    fun typeface(context: Context): Typeface? {
        if (fontFamily == "Default") return null
        if (fontFamily == "default") return null
        if (fontFamily == "デフォルト") return null
        return Typeface.createFromAsset(context.assets, "fonts/" + fontFamily + ".ttf")
    }

    private fun colorIntToBitmap(color: Int?): Bitmap? {
        if (color == null) return null
        val bitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }

    val backgroundColorToBitmap: Bitmap?
        get() =
            colorIntToBitmap(backgroundColor)

    val homeTextColorToBitmap: Bitmap?
        get() =
            colorIntToBitmap(homeTextColor)

    val homeTextShadowColorToBitmap: Bitmap?
        get() =
            colorIntToBitmap(homeTextShadowColor)

    val isNewEntity: Boolean get() = id == 0L
}

class CharacterEditor(val context: Context) {

    private val db = AppDatabase.getDatabase(context)

    private val _state = MutableStateFlow(CharacterEditState())

    val state: StateFlow<CharacterEditState> = _state

    fun setName(v: String) {
        _state.value = _state.value.copy(name = v)
    }

    fun setCreated(v: Date) {
        _state.value = _state.value.copy(created = v)
    }

    fun setIconImage(v: Bitmap?) {
        _state.value = _state.value.copy(iconImage = v)
    }

    fun setBackgroundImage(v: Bitmap?) {
        _state.value = _state.value.copy(backgroundImage = v)
    }

    fun setBackgroundImageOpacity(v: Float) {
        _state.value = _state.value.copy(backgroundImageOpacity = v)
    }

    fun setBackgroundColor(v: Int) {
        _state.value = _state.value.copy(backgroundColor = v)
    }

    fun setHomeTextColor(v: Int) {
        _state.value = _state.value.copy(homeTextColor = v)
    }

    fun setHomeTextShadowColor(v: Int) {
        _state.value = _state.value.copy(homeTextShadowColor = v)
    }

    fun setAboveText(v: String) {
        _state.value = _state.value.copy(aboveText = v)
    }

    fun setBelowText(v: String) {
        _state.value = _state.value.copy(belowText = v)
    }

    fun setIsZeroDayStart(v: Boolean) {
        _state.value = _state.value.copy(isZeroDayStart = v)
    }

    fun setElapsedDateFormat(v: Int) {
        _state.value = _state.value.copy(elapsedDateFormat = v)
    }

    fun setFontFamily(v: String) {
        _state.value = _state.value.copy(fontFamily = v)
    }

    fun addAnniversary(anniversary: CustomAnniversary.Draft) {
        val list = _state.value.anniversaries.toMutableList()
        list.add(anniversary)
        _state.value = _state.value.copy(
            anniversaries = list,
            action = CharacterEditAction.AddAnniversary,
            status = CharacterEditStatus.Success
        )
    }

    fun replaceAnniversary(anniversary: CustomAnniversary.Draft) {
        val list = _state.value.anniversaries.toMutableList()
        val index = list.indexOfFirst { e -> e.uuid == anniversary.uuid }
        if (index != -1) {
            list[index] = anniversary
        }
        _state.value = _state.value.copy(
            anniversaries = list,
            action = CharacterEditAction.ReplaceAnniversary,
            status = CharacterEditStatus.Success
        )
    }

    fun removeAnniversary(pos: Int) {
        val list = _state.value.anniversaries.toMutableList()
        list.removeAt(pos)
        _state.value = _state.value.copy(
            anniversaries = list,
            action = CharacterEditAction.RemoveAnniversary,
            status = CharacterEditStatus.Success
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun setEntityById(id: Long) {
        val entity = db.recommendCharacterDao().findByIdCharacterWithAnniversaries(id)
        if (entity != null) {
            setEntity(entity)
        }
    }

    fun setEntity(entity: CharacterWithAnniversaries? = null) {
        _state.value = CharacterEditState(
            id = entity?.id ?: 0,
            name = entity?.character?.name ?: "",
            created = entity?.character?.created ?: Date(),
            aboveText = entity?.character?.aboveText ?: "を推して",
            belowText = entity?.character?.belowText ?: "になりました",
            homeTextColor = entity?.character?.homeTextColor ?: CharacterEditState.defaultTextColor,
            homeTextShadowColor = entity?.character?.homeTextShadowColor
                ?: CharacterEditState.defaultTextShadowColor,
            backgroundColor = entity?.character?.backgroundColor
                ?: CharacterEditState.defaultBackgroundColor,
            backgroundImageOpacity = entity?.character?.backgroundImageOpacity
                ?: CharacterEditState.defaultBackgroundImageOpacity,
            isZeroDayStart = entity?.character?.isZeroDayStart ?: false,
            fontFamily = entity?.character?.fontFamily ?: "デフォルト",
            iconImage = entity?.character?.getIconImage(context, 500, 500),
            beforeIconImageUri = entity?.character?.iconImageUri,
            backgroundImage = entity?.character?.getBackgroundBitmap(context, 500, 500),
            beforeBackgroundImageUri = entity?.character?.backgroundImageUri,
            anniversaries =
            entity?.anniversaries?.map { it.toDraft() }?.toMutableList() ?: mutableListOf()
        )
    }

    fun save() {
        try {
            if (_state.value.status == CharacterEditStatus.Processing &&
                _state.value.action == CharacterEditAction.Save
            ) {
                return
            }

            _state.value = _state.value.copy(
                action = CharacterEditAction.Save,
                status = CharacterEditStatus.Processing
            )
            db.runInTransaction {
                val account = CharacterPinningManager(context).initialize()
                val entity = RecommendCharacter()
                val state = _state.value
                entity.id = state.id
                entity.accountId = account.id
                entity.name = state.name
                entity.created = state.created
                entity.aboveText = state.aboveText
                entity.belowText = state.belowText
                entity.homeTextColor = state.homeTextColor
                entity.homeTextShadowColor = state.homeTextShadowColor
                entity.backgroundColor = state.backgroundColor
                entity.backgroundImageOpacity =
                    state.backgroundImageOpacity
                entity.isZeroDayStart = state.isZeroDayStart
                entity.fontFamily = state.fontFamily

                state.beforeBackgroundImageUri?.let {
                    if (BitmapUtility.fileExistsByPrivate(
                            context,
                            it
                        )
                    ) {
                        BitmapUtility.deletePrivateImage(context, it)
                    }
                }
                state.backgroundImage?.let {
                    val filename = BitmapUtility.generateFilename()
                    val ext = ".png"
                    BitmapUtility.insertPrivateImage(context, it, filename, ext)
                    entity.backgroundImageUri = filename + ext
                }

                state.beforeIconImageUri?.let {
                    if (BitmapUtility.fileExistsByPrivate(
                            context,
                            it
                        )
                    ) {
                        BitmapUtility.deletePrivateImage(context, it)
                    }
                }
                state.iconImage?.let {
                    val filename = BitmapUtility.generateFilename()
                    val ext = ".png"
                    BitmapUtility.insertPrivateImage(context, it, filename, ext)
                    entity.iconImageUri = filename + ext
                }

                var characterId = entity.id
                if (entity.id != 0L) {
                    db.recommendCharacterDao().updateCharacter(entity)
                } else {
                    characterId = db.recommendCharacterDao().insertCharacter(entity)
                }

                db.customAnniversaryDao().deleteByCharacterId(entity.id)

                state.anniversaries.forEach {
                    it.characterId = characterId
                    db.customAnniversaryDao().insertAnniversary(it.toFinal())
                }

                setEntity(null)

                _state.value = _state.value.copy(
                    action = CharacterEditAction.Save,
                    status = CharacterEditStatus.Success
                )
            }
        } catch (e: Exception) {
            _state.value =
                _state.value.copy(
                    action = CharacterEditAction.Save,
                    status = CharacterEditStatus.Failure,
                    errorMessage = e.message
                )
        }
    }
}

