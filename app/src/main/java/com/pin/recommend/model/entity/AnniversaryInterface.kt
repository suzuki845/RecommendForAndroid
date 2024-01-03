package com.pin.recommend.model.entity

import java.util.Date

interface AnniversaryInterface {

    fun getName(): String
    fun getTopText(): String
    fun getBottomText(): String
    fun getRemainingDays(current: Date): Long?
    fun getElapsedDays(current: Date): Long
    fun isAnniversary(current: Date): Boolean
    fun getMessage(current: Date): String
}

data class Anniversary(
    val name: String = "",
    val topText: String = "",
    val bottomText: String = "",
    val elapsedDays: String = "",
    val message: String = ""
)