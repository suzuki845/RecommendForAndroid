package com.pin.recommend.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.R
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.CharacterWithAnniversaries
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.util.Progress
import java.util.*

class CharacterEditor(val context: Context) {

    companion object {
        val defaultBackgroundColor = Color.parseColor("#00ffffff")
        val defaultBackgroundImageOpacity = 1f
        val defaultTextColor = Color.parseColor("#ff000000")
        val defaultTextShadowColor = Color.parseColor("#00000000")
    }

    private val db = AppDatabase.getDatabase(context)

    private val id = MutableLiveData<Long>(0)

    val name = MutableLiveData<String?>()

    val created = MutableLiveData(Date())

    val iconImage = MutableLiveData<Bitmap?>()

    val iconWithDefaultImage = iconImage.map {
        if(it == null) {
            return@map context.getDrawable(R.drawable.ic_person_300dp)?.toBitmapOrNull()
        }
        return@map it
    }

    private val beforeIconImageUri = MutableLiveData<String?>()

    val backgroundImage = MutableLiveData<Bitmap?>()

    val backgroundImageOpacity = MutableLiveData(defaultBackgroundImageOpacity)

    private val beforeBackgroundImageUri = MutableLiveData<String?>()

    val backgroundColor = MutableLiveData(defaultBackgroundColor)

    val backgroundColorToBitmap = backgroundColor.map {
        colorIntToBitmap(it)
    }

    val homeTextColor = MutableLiveData(defaultTextColor)

    val homeTextColorToBitmap = homeTextColor.map {
        colorIntToBitmap(it)
    }

    val homeTextShadowColor = MutableLiveData(defaultTextShadowColor)

    val homeTextShadowColorToBitmap = homeTextShadowColor.map {
        colorIntToBitmap(it)
    }

    val aboveText = MutableLiveData("を推して")

    val belowText = MutableLiveData("になりました")

    val isZeroDayStart = MutableLiveData(false)

    val elapsedDateFormat = MutableLiveData(0)

    val fontFamily = MutableLiveData("デフォルト")

    val typeface = fontFamily.map {
        if(it == null) return@map null
        if(it == "Default") return@map null
        if(it == "default") return@map null
        if(it == "デフォルト") return@map  null
        return@map Typeface.createFromAsset(context.assets, "fonts/" + it + ".ttf")
    }

    val anniversaries = MutableLiveData<MutableList<CustomAnniversary.Draft>>(mutableListOf())

    private fun colorIntToBitmap(color: Int?): Bitmap?{
        if(color == null) return null
        val bitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }

    fun initialize(id: Long){
        val entity = db.recommendCharacterDao().findByIdCharacterWithAnniversaries(id)
        if (entity != null){
            initialize(entity)
        }
    }

    open fun initialize(entity: CharacterWithAnniversaries? = null) {
        id.value = entity?.id
        name.value = entity?.character?.name
        created.value = entity?.character?.created ?: Date()
        aboveText.value = entity?.character?.aboveText
        belowText.value = entity?.character?.belowText
        homeTextColor.value = entity?.character?.homeTextColor
        homeTextShadowColor.value = entity?.character?.homeTextShadowColor
        backgroundColor.value = entity?.character?.backgroundColor
        backgroundImageOpacity.value = entity?.character?.backgroundImageOpacity ?: 1f
        isZeroDayStart.value = entity?.character?.isZeroDayStart
        fontFamily.value = entity?.character?.fontFamily
        iconImage.value = entity?.character?.getIconImage(context, 500, 500)
        beforeIconImageUri.value = entity?.character?.iconImageUri
        backgroundImage.value = entity?.character?.getBackgroundBitmap(context, 500, 500)
        beforeBackgroundImageUri.value = entity?.character?.backgroundImageUri
        anniversaries.value = entity?.anniversaries?.map { it.toDraft() }?.toMutableList() ?: mutableListOf()
    }

    fun addAnniversary(anniversary: CustomAnniversary.Draft){
        val list = anniversaries.value ?: mutableListOf()
        list.add(anniversary)
        anniversaries.value = list
    }

    fun replaceAnniversary(anniversary: CustomAnniversary.Draft){
        val list = anniversaries.value ?: mutableListOf()
        val index = list.indexOfFirst { e -> e.uuid == anniversary.uuid }
        if(index != -1){
            list[index] = anniversary
        }

        anniversaries.value = list
    }

    fun removeAnniversary(pos: Int){
        val items = anniversaries.value ?: mutableListOf()
        items.removeAt(pos)
        anniversaries.value = items
    }

    fun save(p: Progress){
        try {
            p.onStart()

            db.runInTransaction {
                val account = AccountModel(context).initialize()
                val entity = RecommendCharacter()
                entity.id = id.value ?: 0
                entity.accountId = account.id
                entity.name = name.value
                entity.created = created.value ?: throw Exception("Date is null")
                entity.aboveText = aboveText.value
                entity.belowText = belowText.value
                entity.homeTextColor = homeTextColor.value
                entity.homeTextShadowColor = homeTextShadowColor.value
                entity.backgroundColor = backgroundColor.value ?: defaultBackgroundColor
                entity.backgroundImageOpacity = backgroundImageOpacity.value ?: defaultBackgroundImageOpacity
                entity.isZeroDayStart = isZeroDayStart.value ?: false
                entity.fontFamily = fontFamily.value

                beforeBackgroundImageUri.value?.let {
                    if (BitmapUtility.fileExistsByPrivate(
                            context,
                            it
                        )
                    ) {
                        BitmapUtility.deletePrivateImage(context, it)
                    }
                }
                backgroundImage.value?.let {
                    val filename = BitmapUtility.generateFilename()
                    val ext = ".png"
                    BitmapUtility.insertPrivateImage(context, it, filename, ext)
                    entity.backgroundImageUri = filename + ext
                }

                beforeIconImageUri.value?.let {
                    if (BitmapUtility.fileExistsByPrivate(
                            context,
                            it
                        )
                    ) {
                        BitmapUtility.deletePrivateImage(context, it)
                    }
                }
                iconImage.value?.let {
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

                anniversaries.value?.forEach {
                    it.characterId = characterId
                    db.customAnniversaryDao().insertAnniversary(it.toFinal())
                }

                initialize(null)
            }

            p.onComplete()
        } catch (e: Exception) {
            p.onError(e)
        }
    }
}

