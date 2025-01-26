package com.pin.recommend.domain.entity

import android.content.res.AssetManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

open class CustomFont(val name: String, val isPrivilege: Boolean) {
    val id = this.name

    fun getFontFamily(
        assets: AssetManager,
    ): FontFamily? {
        if (name == "Default") return null
        if (name == "default") return null
        if (name == "デフォルト") return null

        return FontFamily(
            Font(
                assetManager = assets,
                path = "fonts/$name.ttf"
            )
        )
    }

}
