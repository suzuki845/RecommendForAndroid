package com.pin.recommend.model

import android.content.Context
import androidx.lifecycle.map
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.entity.Account

class AccountModel(
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

    fun initialize() : Account{
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