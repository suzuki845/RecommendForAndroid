package com.pin.recommend.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.google.gson.Gson
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.dao.CustomAnniversaryDao
import com.pin.recommend.model.dao.RecommendCharacterDao
import com.pin.recommend.model.entity.*
import com.pin.recommend.util.combine2
import com.pin.recommend.util.combine3
import java.util.*

class CharacterDetails(
    private val context: Context,
    private val accountDao: AccountDao,
    private val characterDao: RecommendCharacterDao,
) {


    val id = MutableLiveData<Long?>()

    private val _displayOnHomeAnniversaries = MutableLiveData<List<AnniversaryInterface>>(listOf())

    val cwa = id.switchMap {
        return@switchMap characterDao.watchByIdCharacterWithAnniversaries(it ?: -1)
    }

    private val account = accountDao.findTrackedById(Account.ACCOUNT_ID.toLong())

    private val displayOnHomeAnniversary = _displayOnHomeAnniversaries.map {
        val a = it.firstOrNull()
        Anniversary(
            a?.getName() ?: "",
            a?.getTopText() ?: "",
            a?.getBottomText() ?: "",
            a?.getElapsedDays(Date())?.let { d -> "${d}日" } ?: "",
            a?.getMessage(Date()) ?: ""
        )
    }

    fun changeDisplayOnHomeAnniversary() {
        val current = LinkedList<AnniversaryInterface>()
        _displayOnHomeAnniversaries.value?.forEach {
            current.add(it)
        }
        current.addFirst(current.removeLast())
        _displayOnHomeAnniversaries.value = current
    }

    fun initialize() {
        cwa.observeForever {
            _displayOnHomeAnniversaries.value = it?.anniversaries()
        }
    }

    val state = combine3(account, cwa, displayOnHomeAnniversary) { a, b, c ->
        return@combine3 State(
            b?.id ?: -1,
            a?.fixedCharacterId != null,
            b?.character?.name ?: "",
            b?.appearance(context) ?: Appearance(),
            c ?: Anniversary(),
            b?.character?.storySortOrder ?: 0
        )
    }

    fun pinning() {
        val account = accountDao.findById(Account.ACCOUNT_ID.toLong())
        account.fixedCharacterId = id.value
        AppDatabase.executor.execute { accountDao.updateAccount(account) }
    }

    fun unpinning() {
        val account = accountDao.findById(Account.ACCOUNT_ID.toLong())
        account.fixedCharacterId = null
        AppDatabase.executor.execute { accountDao.updateAccount(account) }
    }

    fun updateStorySortOrder(order: Int) {
        val character = cwa.value?.character
        character?.storySortOrder = 0
        AppDatabase.executor.execute { characterDao.updateCharacter(character) }
    }


    data class State(
        val characterId: Long,
        val isPinning: Boolean = false,
        val characterName: String,
        val appearance: Appearance,
        val anniversary: Anniversary,
        val storySortOrder: Int,
    ){
        fun toJson(): String {
            return Gson().toJson(this)
        }

        companion object {
            fun fromJson(json: String): State {
                return Gson().fromJson(json, State::class.java)
            }
        }
    }

}