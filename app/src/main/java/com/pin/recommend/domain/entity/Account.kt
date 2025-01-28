package com.pin.recommend.domain.entity

import android.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Account  {
    //json化するためのフィールド
    @Ignore
    var characters: List<RecommendCharacter> = mutableListOf()

    @Ignore
    var paymentTags: List<PaymentTag> = mutableListOf()

    @JvmField
    @PrimaryKey
    var id = ACCOUNT_ID
    @JvmField
    var fixedCharacterId: Long? = null
    @JvmField
    var toolbarBackgroundColor = Color.parseColor("#eb34ab")
    @JvmField
    var toolbarTextColor = Color.parseColor("#ffffff")
    companion object {
        //ここまで
        @Ignore
        val ACCOUNT_ID = 1L
    }
}