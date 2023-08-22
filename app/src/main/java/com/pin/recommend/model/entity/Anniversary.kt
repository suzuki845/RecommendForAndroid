package com.pin.recommend.model.entity

import androidx.core.graphics.createBitmap
import com.pin.recommend.model.value.PeriodType
import com.pin.recommend.util.TimeUtil
import java.util.*


class UserDefinedAnniversary: AnniversaryInterface {
    private val name: String
    private val date: Date
    private val isZeroDayStart: Boolean
    private val topText: String
    private val bottomText: String

    constructor(name: String, date: Date, isZeroDayStart: Boolean, topText: String = "", bottomText: String = ""){
        this.name = name
        this.isZeroDayStart = isZeroDayStart
        this.date = date
        TimeUtil.resetDate(this.date)
        this.topText = topText
        this.bottomText = bottomText
    }

    override fun getName(): String{
        return name
    }

    override fun getTopText(): String {
        return topText
    }

    override fun getBottomText(): String {
        return bottomText
    }

    fun getDate() : Date{
        return date
    }

    override fun getElapsedDays(current: Date): Long {
        val now = Calendar.getInstance().apply { time = TimeUtil.resetDate(current)}
        val anniversary = Calendar.getInstance().apply { time = getDate()}
        return TimeUtil.getDiffDays(now, anniversary)
    }

    override fun getRemainingDays(current: Date) : Long?{
        val now = Calendar.getInstance()
        now.time = current
        TimeUtil.resetTime(now)

        val anniversary = Calendar.getInstance().apply { time = getDate() }
        val remaining = TimeUtil.getDiffDays(now, anniversary)

        if(remaining > 0){
            return 365 - remaining
        }
        return -remaining
    }

    override fun isAnniversary(current: Date): Boolean {
        val cc = Calendar.getInstance().apply { time = current }
        val ac = Calendar.getInstance().apply { time = date }

        return cc.get(Calendar.MONTH) == ac.get(Calendar.MONTH)
                && cc.get(Calendar.DAY_OF_MONTH) == ac.get(Calendar.DAY_OF_MONTH)
    }

    override fun getMessage(current: Date) : String{
        if(isAnniversary(current)){
            return getName() + "記念になりました！"
        }

        val remaining = getRemainingDays(current)
        if(remaining != null){
            return "${getName()}記念まであと ${current} 日"
        }
        return ""
    }

}

class SystemDefinedAnniversary: AnniversaryInterface {

    private val name: String
    private val startDate = Calendar.getInstance()
    private val farInAdvance: Int
    private val periodType: PeriodType
    private val isZeroDayStart: Boolean
    private val topText: String
    private val bottomText: String

    constructor(name: String, startDate: Date, periodType: PeriodType, farInAdvance: Int, isZeroDayStart: Boolean, topText: String, bottomText: String){
        this.name = name
        this.startDate.time = startDate
        this.periodType = periodType
        this.isZeroDayStart = isZeroDayStart
        this.farInAdvance = farInAdvance
        TimeUtil.resetTime(this.startDate)
        this.topText = topText
        this.bottomText = bottomText
    }

    override fun getName(): String{
        return name
    }

    override fun getTopText(): String {
        return topText
    }

    override fun getBottomText(): String {
        return bottomText
    }

    fun getDate() : Date{
        val anniversary = Calendar.getInstance()
        anniversary.time = startDate.time
        if (!isZeroDayStart) {
            anniversary.add(Calendar.DAY_OF_MONTH, -1)
        }
        when (periodType) {
            PeriodType.Year -> {
                anniversary.add(Calendar.YEAR, farInAdvance)
            }
            PeriodType.Month -> {
                anniversary.add(Calendar.MONTH, farInAdvance)
            }
            PeriodType.Days -> {
                anniversary.add(Calendar.DAY_OF_MONTH, farInAdvance)
            }
        }
        return anniversary.time
    }

    override fun getElapsedDays(current: Date): Long {
        val now = Calendar.getInstance().apply { time = TimeUtil.resetDate(current)}
        val anniversary = Calendar.getInstance().apply { time = getDate()}
        return TimeUtil.getDiffDays(now, anniversary)
    }

    override fun getRemainingDays(current: Date) : Long?{
        val now = Calendar.getInstance()
        now.time = current
        TimeUtil.resetTime(now)

        val anniversary = Calendar.getInstance().apply { time = getDate() }
        val remaining = TimeUtil.getDiffDays(anniversary, now)

        if(remaining < 0){
            return null
        }
        return remaining
    }

    override fun isAnniversary(current: Date): Boolean {
        return TimeUtil.resetDate(current).compareTo(TimeUtil.resetDate(getDate())) == 0
    }

    override fun getMessage(current: Date): String {
        return "${getElapsedDays(current)}日"
    }

}

class SystemDefinedAnniversaries: AnniversaryInterface {

    private var character: RecommendCharacter

    private val anniversaries: MutableList<SystemDefinedAnniversary> = mutableListOf()

    constructor(character: RecommendCharacter){
        this.character = character
    }

    fun initialize(){
        val created = Calendar.getInstance()
        created.time = character.created
        TimeUtil.resetTime(created)
        anniversaries.clear()
        for (i in 1..18){
            val unit = i * 100
            anniversaries.add(SystemDefinedAnniversary("${unit}日", startDate = created.time, periodType = PeriodType.Days, farInAdvance = unit, isZeroDayStart = character.isZeroDayStart, topText = character.aboveText ?: "", bottomText = character.belowText ?: ""))
        }
        for (i in 1..100){
            val unit = i
            anniversaries.add(SystemDefinedAnniversary("${unit}周年", startDate = created.time, periodType = PeriodType.Year, farInAdvance = unit, isZeroDayStart = character.isZeroDayStart, topText = character.aboveText ?: "", bottomText = character.belowText ?: ""))
        }
    }

    private fun getNextAnniversary(current: Date) : SystemDefinedAnniversary?{
        val c = Calendar.getInstance()
        c.time = current
        return anniversaries.firstOrNull{
            it.getDate() > c.time
        }
    }

    private fun getCurrentAnniversary(current: Date): SystemDefinedAnniversary?{
        val isInAnniversary = anniversaries.firstOrNull{
            it.isAnniversary(current)
        }
        if(isInAnniversary != null){
            return isInAnniversary
        }
        return getNextAnniversary(current)
    }

    override fun isAnniversary(current: Date): Boolean{
        return anniversaries.find{
            it.isAnniversary(current)
        } != null
    }

    override fun getName(): String {
        return getCurrentAnniversary(Date())?.let { it.getName() } ?: ""
    }

    override fun getTopText(): String {
        return getCurrentAnniversary(Date())?.let { it.getTopText() } ?: ""
    }

    override fun getBottomText(): String {
        return getCurrentAnniversary(Date())?.let { it.getBottomText() } ?: ""
    }

    override fun getRemainingDays(current: Date): Long? {
        return getCurrentAnniversary(current)?.let { it.getRemainingDays(current) }
    }

    override fun getElapsedDays(current: Date): Long {
        return character.getElapsedDays(current)
    }

    override fun getMessage(current: Date) : String{
        val isToday = anniversaries.find{
            it.isAnniversary(current)
        }?.let {
            it.getName()
        }
        if(isToday != null){
            return isToday + "記念になりました！"
        }

        val anniversary = getNextAnniversary(current)

        return anniversary?.getRemainingDays(current)?.let {
            "${anniversary.getName()}記念まであと ${it} 日"
        } ?: ""
    }


}







