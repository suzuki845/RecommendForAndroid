package com.pin.recommend.domain.entity.translation

import android.graphics.Color
import com.pin.recommend.domain.entity.Account

class AccountExportable{
    var characters: List<RecommendCharacterExportable> = mutableListOf()

    var toolbarBackgroundColor: String? = null

    var toolbarTextColor: String? = null

    constructor(account: Account){
        account.toolbarBackgroundColor?.let{
            toolbarBackgroundColor = String.format("#%06X", 0xFFFFFF and it)
        }
        account.toolbarTextColor?.let {
            toolbarTextColor = String.format("#%06X", 0xFFFFFF and it)
        }
    }

    fun importable(): Account{
        val account = Account()
        account.id = Account.ACCOUNT_ID
        toolbarTextColor?.let {
            try{
                account.toolbarTextColor = Color.parseColor(it)
            }catch(e: Exception){

            }
        }
        toolbarBackgroundColor?.let {
            try{
                account.toolbarBackgroundColor = Color.parseColor(it)
            }catch(e: Exception){

            }
        }
        account.characters = characters.map { it.importable() }

        return account
    }

}