package com.pin.recommend.model.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.dao.AccountDao
import com.pin.recommend.model.entity.Account

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: AccountDao = AppDatabase.getDatabase(application.applicationContext).accountDao()
    val accountLiveData: LiveData<Account>

    init{
        initializeAccount()
        accountLiveData = dao.findTrackedById(Account.ACCOUNT_ID.toLong())
    }

    private fun initializeAccount() {
        AppDatabase.executor.execute {
            var model = dao.findById(Account.ACCOUNT_ID.toLong())
            if (model == null) {
                model = Account()
                dao.insertAccount(model)
            }
        }
    }

    fun saveAccount(account: Account?) {
        AppDatabase.executor.execute { dao.updateAccount(account) }
    }

}