package com.pin.recommend.model.entity.translation

import android.graphics.Color
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.entity.Story
import java.util.*

class RecommendCharacterExportable {

    var payments: List<PaymentExportable> = mutableListOf()

    var events: List<EventExportable> = mutableListOf()

    var stories: List<StoryExportable> = mutableListOf()

    var anniversaries: List<CustomAnniversaryExportable> = mutableListOf()

    var name: String? = null

    var created: Date? = null

    var iconImageSrc: String? = null

    var backgroundImageSrc: String? = null

    var backgroundColor: String? = null

    var toolbarBackgroundColor: String? = null

    var toolbarTextColor: String? = null

    var textColor: String? = null

    var topText: String? = null

    var bottomText: String? = null

    var isZeroDayStart = false

    var elapsedDateType = 0

    var fontType: String? = null

    var storySortOrder = 0

    var backgroundImageOpacity = 1f

    constructor(character: RecommendCharacter){
        name = character.name
        created = character.created
        isZeroDayStart = character.isZeroDayStart
        elapsedDateType = character.elapsedDateFormat
        character.aboveText?.let {
            topText = it
        }
        character.belowText?.let{
            bottomText = it
        }
        character.backgroundColor?.let {
            backgroundColor = String.format("#%06X", 0xFFFFFF and it)
        }
        character.homeTextColor?.let {
            textColor = String.format("#%06X", 0xFFFFFF and it)
        }
        character.iconImageUri?.let {
            iconImageSrc = it
        }
        character.backgroundImageUri?.let {
            backgroundImageSrc = it
        }
        character.fontFamily?.let {
            fontType = it
        }
        character.toolbarBackgroundColor?.let{
            toolbarBackgroundColor = String.format("#%06X", 0xFFFFFF and it)
        }
        character.toolbarTextColor?.let {
            toolbarTextColor = String.format("#%06X", 0xFFFFFF and it)
        }

    }

    fun importable() : RecommendCharacter{
        val character = RecommendCharacter()
        character.belowText = bottomText
        character.aboveText = topText
        character.fontFamily = fontType
        character.backgroundImageUri = backgroundImageSrc
        character.iconImageUri = iconImageSrc
        character.created = created  ?: Date()
        character.isZeroDayStart = isZeroDayStart
        character.elapsedDateFormat = elapsedDateType
        character.name = name
        backgroundColor?.let {
            try{
                character.backgroundColor = Color.parseColor(it)
            }catch(e: Exception){

            }
        }
        textColor?.let {
            try{
                character.homeTextColor = Color.parseColor(it)
            }catch(e: Exception){}
        }
        toolbarBackgroundColor?.let {
            try{
                character.toolbarBackgroundColor = Color.parseColor(it)
            }catch(e: Exception){
            }
        }
        toolbarTextColor?.let {
            try{
                character.toolbarTextColor = Color.parseColor(it)
            }catch(e: Exception){

            }
        }
        character.stories = stories.map { it.importable() }
        character.events = events.map { it.importable() }
        character.payments = payments.map { it.importable() }
        character.anniversaries = anniversaries.map { it.importable() }

        return character
    }

}