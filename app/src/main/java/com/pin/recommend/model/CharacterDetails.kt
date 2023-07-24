package com.pin.recommend.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.pin.recommend.model.dao.CustomAnniversaryDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.AnniversaryInterface
import com.pin.recommend.model.entity.SystemDefinedAnniversaries
import com.pin.recommend.model.entity.UserDefinedAnniversary
import com.pin.recommend.util.combine2
import java.util.*

data class CharacterDetailsState(
    val characterId: Long,
    val iconImage: Bitmap?,
    val characterName: String,
    val topText: String,
    val bottomText: String,
    val elapsedDays: Long,
    val anniversaryMessage: String,
    val fontFamily: String?,
    val textColor: Int,
    val textShadowColor: Int
)

class CharacterDetails(
    private val context: Context,
    private val characterDao: RecommendCharacterDao,
    private val customAnniversaryDao: CustomAnniversaryDao,
) {

    private val _id = MutableLiveData<Long?>()

    fun setId(id: Long?) {
        _id.value = id
    }

    private val _displayOnHomeAnniversaries = MutableLiveData<List<AnniversaryInterface>>(listOf())

    val character = _id.switchMap {
        return@switchMap characterDao.findTrackedById(it ?: -1)
    }

    private val systemDefinedAnniversaries: LiveData<AnniversaryInterface?> = character.map {
        if(it == null) return@map null
        return@map SystemDefinedAnniversaries(it)
    }

    private val userDefinedAnniversaries: LiveData<List<AnniversaryInterface>> =
        character.switchMap { character ->
            if (character == null) return@switchMap MutableLiveData(listOf())
            return@switchMap customAnniversaryDao.watchByCharacterId(character.id).map { list ->
                return@map list.map {
                    return@map UserDefinedAnniversary(
                        name = it.name,
                        date = it.date,
                        isZeroDayStart = character.isZeroDayStart,
                        topText = it.topText ?: "",
                        bottomText = it.bottomText ?: ""
                    )
                }
            }
        }

    private val allAnniversaries: LiveData<List<AnniversaryInterface>> =
        combine2(systemDefinedAnniversaries, userDefinedAnniversaries) { a, b ->
            val result = arrayListOf<AnniversaryInterface>()
            if (a != null) {
                result.add(a)
            }
            result.addAll(b ?: arrayListOf())
            return@combine2 result
        }

    private val displayOnHomeAnniversary = _displayOnHomeAnniversaries.map { it.firstOrNull() }

    fun changeDisplayOnHomeAnniversary() {
        val current = LinkedList<AnniversaryInterface>()
        allAnniversaries.value?.forEach {
            current.add(it)
        }
        current.addFirst(current.removeLast())
        _displayOnHomeAnniversaries.value = current
    }

    fun initialize() {
        //_displayOnHomeAnniversaries.value = allAnniversaries.value ?: listOf()
        allAnniversaries.observeForever {
            _displayOnHomeAnniversaries.value = it
        }
    }

    val state = combine2(character, allAnniversaries.map { it.firstOrNull() }) { a, b ->
        return@combine2 CharacterDetailsState(
            a?.id ?: -1,
            a?.getIconImage(context, 500, 500),
            a?.name ?: "",
            b?.getTopText() ?: "",
            b?.getTopText() ?: "",
            b?.getElapsedDays(Date()) ?: -1,
            b?.getMessage(Date()) ?: "",
            a?.fontFamily,
            a?.homeTextColor ?: Color.BLACK,
            a?.homeTextShadowColor ?: Color.parseColor("#00000000")
        )
    }

}