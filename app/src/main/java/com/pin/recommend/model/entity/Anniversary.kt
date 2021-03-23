package com.pin.recommend.model.entity

import com.pin.recommend.util.TimeUtil
import java.util.*

class Anniversary {

    private val name: String
    private val whenDays: Int
    private var startDate = Calendar.getInstance()

    constructor(name: String, whenDays: Int, startDate: Date){
        this.name = name
        this.whenDays = whenDays
        this.startDate.time = startDate
        TimeUtil.resetTime(this.startDate)
    }

    fun getName(): String{
        return name
    }

    fun getAnniversary(isZeroDayStart: Boolean) : Calendar{
        val anniversary = Calendar.getInstance()
        anniversary.time = startDate.time
        if (!isZeroDayStart) {
            anniversary.add(Calendar.DAY_OF_MONTH, -1)
        }
        anniversary.add(Calendar.DAY_OF_MONTH, whenDays)
        return anniversary
    }

    fun getRemainingTime(currentDate: Date, isZeroDayStart: Boolean) : Long?{
        val remaining = Calendar.getInstance()
        remaining.time = currentDate
        TimeUtil.resetTime(remaining)

        val anniversary = getAnniversary(isZeroDayStart)
        val diff = TimeUtil.getDiffDays(anniversary, remaining)

        if(diff < 0){
            return null
        }
        return diff
    }

}

class AnniversaryManager {

    private var character: RecommendCharacter

    private val anniversaries: MutableList<Anniversary> = mutableListOf()

    constructor(character: RecommendCharacter){
        this.character = character
        initialize(character)
    }

    fun initialize(character: RecommendCharacter){
        this.character = character
        val created = Calendar.getInstance()
        created.time = character.created
        TimeUtil.resetTime(created)
        anniversaries.clear()
        anniversaries.add(Anniversary("１００日" , 100, created.time))
        anniversaries.add(Anniversary("２００日" , 200, created.time))
        anniversaries.add(Anniversary("３００日" , 300, created.time))
        anniversaries.add(Anniversary("１周年" , 365, created.time))
        anniversaries.add(Anniversary("４００日" , 400, created.time))
        anniversaries.add(Anniversary("５００日" , 500, created.time))
        anniversaries.add(Anniversary("６００日" , 600, created.time))
        anniversaries.add(Anniversary("７００日" , 700, created.time))
        anniversaries.add(Anniversary("２周年" , 365*2, created.time))
        anniversaries.add(Anniversary("８００日" , 800, created.time))
        anniversaries.add(Anniversary("９００日" , 900, created.time))
        anniversaries.add(Anniversary("１０００日" , 1000, created.time))
        anniversaries.add(Anniversary("３周年" , 365*3, created.time))
        anniversaries.add(Anniversary("１１００日" , 1100, created.time))
        anniversaries.add(Anniversary("１２００日" , 1200, created.time))
        anniversaries.add(Anniversary("１３００日" , 1300, created.time))
        anniversaries.add(Anniversary("１４００日" , 1400, created.time))
        anniversaries.add(Anniversary("４周年" , 365*4, created.time))
        anniversaries.add(Anniversary("１５００日" , 1500, created.time))
        anniversaries.add(Anniversary("１６００日" , 1600, created.time))
        anniversaries.add(Anniversary("１７００日" , 1700, created.time))
        anniversaries.add(Anniversary("１８００日" , 1800, created.time))
        anniversaries.add(Anniversary("５周年" , 365*5, created.time))
        anniversaries.add(Anniversary("６周年" , 365*6, created.time))
        anniversaries.add(Anniversary("７周年" , 365*7, created.time))
        anniversaries.add(Anniversary("８周年" , 365*8, created.time))
        anniversaries.add(Anniversary("９周年" , 365*9, created.time))
        anniversaries.add(Anniversary("１０周年" , 365*10, created.time))
        anniversaries.add(Anniversary("１１周年" , 365*11, created.time))
        anniversaries.add(Anniversary("１２周年" , 365*12, created.time))
        anniversaries.add(Anniversary("１３周年" , 365*13, created.time))
        anniversaries.add(Anniversary("１４周年" , 365*14, created.time))
        anniversaries.add(Anniversary("１５周年" , 365*15, created.time))
        anniversaries.add(Anniversary("１６周年" , 365*16, created.time))
        anniversaries.add(Anniversary("１７周年" , 365*17, created.time))
        anniversaries.add(Anniversary("１８周年" , 365*18, created.time))
        anniversaries.add(Anniversary("１９周年" , 365*19, created.time))
        anniversaries.add(Anniversary("２０周年" , 365*20, created.time))
        anniversaries.add(Anniversary("２１周年" , 365*21, created.time))
        anniversaries.add(Anniversary("２２周年" , 365*22, created.time))
        anniversaries.add(Anniversary("２３周年" , 365*23, created.time))
        anniversaries.add(Anniversary("２４周年" , 365*24, created.time))
        anniversaries.add(Anniversary("２５周年" , 365*25, created.time))
        anniversaries.add(Anniversary("２６周年" , 365*26, created.time))
        anniversaries.add(Anniversary("２７周年" , 365*27, created.time))
        anniversaries.add(Anniversary("２８周年" , 365*28, created.time))
        anniversaries.add(Anniversary("２９周年" , 365*29, created.time))
        anniversaries.add(Anniversary("３０周年" , 365*30, created.time))
        anniversaries.add(Anniversary("３１周年" , 365*31, created.time))
        anniversaries.add(Anniversary("３２周年" , 365*32, created.time))
        anniversaries.add(Anniversary("３３周年" , 365*33, created.time))
        anniversaries.add(Anniversary("３４周年" , 365*34, created.time))
        anniversaries.add(Anniversary("３５周年" , 365*35, created.time))
        anniversaries.add(Anniversary("３６周年" , 365*36, created.time))
        anniversaries.add(Anniversary("３７周年" , 365*37, created.time))
        anniversaries.add(Anniversary("３８周年" , 365*38, created.time))
        anniversaries.add(Anniversary("３９周年" , 365*49, created.time))
        anniversaries.add(Anniversary("４０周年" , 365*40, created.time))
        anniversaries.add(Anniversary("４１周年" , 365*41, created.time))
        anniversaries.add(Anniversary("４２周年" , 365*42, created.time))
        anniversaries.add(Anniversary("４３周年" , 365*43, created.time))
        anniversaries.add(Anniversary("４４周年" , 365*44, created.time))
        anniversaries.add(Anniversary("４５周年" , 365*45, created.time))
        anniversaries.add(Anniversary("４６周年" , 365*46, created.time))
        anniversaries.add(Anniversary("４７周年" , 365*47, created.time))
        anniversaries.add(Anniversary("４８周年" , 365*48, created.time))
        anniversaries.add(Anniversary("４９周年" , 365*49, created.time))
        anniversaries.add(Anniversary("５０周年" , 365*50, created.time))
        anniversaries.add(Anniversary("５１周年" , 365*51, created.time))
        anniversaries.add(Anniversary("５２周年" , 365*52, created.time))
        anniversaries.add(Anniversary("５３周年" , 365*53, created.time))
        anniversaries.add(Anniversary("５４周年" , 365*54, created.time))
        anniversaries.add(Anniversary("５５周年" , 365*55, created.time))
        anniversaries.add(Anniversary("５６周年" , 365*56, created.time))
        anniversaries.add(Anniversary("５７周年" , 365*57, created.time))
        anniversaries.add(Anniversary("５８周年" , 365*58, created.time))
        anniversaries.add(Anniversary("５９周年" , 365*59, created.time))
        anniversaries.add(Anniversary("６０周年" , 365*60, created.time))
        anniversaries.add(Anniversary("６１周年" , 365*61, created.time))
        anniversaries.add(Anniversary("６２周年" , 365*62, created.time))
        anniversaries.add(Anniversary("６３周年" , 365*63, created.time))
        anniversaries.add(Anniversary("６４周年" , 365*64, created.time))
        anniversaries.add(Anniversary("６５周年" , 365*65, created.time))
        anniversaries.add(Anniversary("６６周年" , 365*66, created.time))
        anniversaries.add(Anniversary("６７周年" , 365*67, created.time))
        anniversaries.add(Anniversary("６８周年" , 365*68, created.time))
        anniversaries.add(Anniversary("６９周年" , 365*69, created.time))
        anniversaries.add(Anniversary("７０周年" , 365*70, created.time))
        anniversaries.add(Anniversary("７１周年" , 365*71, created.time))
        anniversaries.add(Anniversary("７２周年" , 365*72, created.time))
        anniversaries.add(Anniversary("７３周年" , 365*73, created.time))
        anniversaries.add(Anniversary("７４周年" , 365*74, created.time))
        anniversaries.add(Anniversary("７５周年" , 365*75, created.time))
        anniversaries.add(Anniversary("７６周年" , 365*76, created.time))
        anniversaries.add(Anniversary("７７周年" , 365*77, created.time))
        anniversaries.add(Anniversary("７８周年" , 365*78, created.time))
        anniversaries.add(Anniversary("７９周年" , 365*79, created.time))
        anniversaries.add(Anniversary("８０周年" , 365*80, created.time))
        anniversaries.add(Anniversary("８１周年" , 365*81, created.time))
        anniversaries.add(Anniversary("８２周年" , 365*82, created.time))
        anniversaries.add(Anniversary("８３周年" , 365*83, created.time))
        anniversaries.add(Anniversary("８４周年" , 365*84, created.time))
        anniversaries.add(Anniversary("８５周年" , 365*85, created.time))
        anniversaries.add(Anniversary("８６周年" , 365*86, created.time))
        anniversaries.add(Anniversary("８７周年" , 365*87, created.time))
        anniversaries.add(Anniversary("８８周年" , 365*88, created.time))
        anniversaries.add(Anniversary("８９周年" , 365*89, created.time))
        anniversaries.add(Anniversary("９０周年" , 365*90, created.time))
        anniversaries.add(Anniversary("９１周年" , 365*91, created.time))
        anniversaries.add(Anniversary("９２周年" , 365*92, created.time))
        anniversaries.add(Anniversary("９３周年" , 365*93, created.time))
        anniversaries.add(Anniversary("９４周年" , 365*94, created.time))
        anniversaries.add(Anniversary("９５周年" , 365*95, created.time))
        anniversaries.add(Anniversary("９６周年" , 365*96, created.time))
        anniversaries.add(Anniversary("９７周年" , 365*97, created.time))
        anniversaries.add(Anniversary("９８周年" , 365*98, created.time))
        anniversaries.add(Anniversary("９９周年" , 365*99, created.time))
        anniversaries.add(Anniversary("１００周年" , 365*100, created.time))
    }

    private fun getNextAnniversary(now: Date) : Anniversary?{
        val c = Calendar.getInstance()
        c.time = now
        return anniversaries.find{
            it.getAnniversary(character.isZeroDayStart) > c
        }
    }

    private fun isAnniversary(now: Date) : Anniversary?{
        val c = Calendar.getInstance()
        c.time = now
        TimeUtil.resetTime(c)
        return anniversaries.find{
             it.getAnniversary(character.isZeroDayStart).compareTo(c) == 0
        }
    }

    fun nextOrIsAnniversary(now: Date) : String?{
        val isToday = isAnniversary(now)?.let {
            it.getName()
        }
        if(isToday != null){
            return isToday + "記念になりました！"
        }

        val anniversary = getNextAnniversary(now)

        return anniversary?.getRemainingTime(now, character.isZeroDayStart)?.let {
                "${anniversary.getName()}記念まであと ${it} 日"
        }
    }

}







