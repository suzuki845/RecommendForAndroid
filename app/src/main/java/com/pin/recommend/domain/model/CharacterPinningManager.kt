package com.pin.recommend.domain.model

import android.content.Context
import androidx.lifecycle.map
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.Account

class CharacterPinningManager(
    val context: Context,
) {
    private val db = AppDatabase.getDatabase(context)

    val entity = db.accountDao().watchById(Account.ACCOUNT_ID).map {
        if (it == null) {
            val a = Account()
            AppDatabase.executor.execute { db.accountDao().insertAccount(a) }
            return@map a
        }
        return@map it
    }

    fun initialize(): Account {
        val account = db.accountDao().findById(Account.ACCOUNT_ID)
        if (account == null) {
            val a = Account()
            AppDatabase.executor.execute { db.accountDao().insertAccount(a) }
            return a
        }
        return account
    }

    fun pinning(id: Long) {
        entity.value?.let {
            val dao = db.accountDao()
            it.fixedCharacterId = id
            AppDatabase.executor.execute { dao.updateAccount(it) }
        }
    }

    fun unpinning() {
        entity.value?.let {
            val dao = db.accountDao()
            it.fixedCharacterId = null
            AppDatabase.executor.execute { dao.updateAccount(it) }
        }
    }


}