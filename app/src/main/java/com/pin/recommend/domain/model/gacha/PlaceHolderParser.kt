package com.pin.recommend.domain.model.gacha

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan

class PlaceholderParser(private var template: String) {

    fun parse(
        values: List<String>,
        font: Typeface,
        placeholderFont: Typeface
    ): SpannableStringBuilder {
        // 初期のSpannableStringBuilderを作成
        val spannableString = SpannableStringBuilder(template)

        // プレースホルダーを順に処理
        for ((index, value) in values.withIndex()) {
            val placeholder = "$$index"
            val startIndex = spannableString.indexOf(placeholder)

            if (startIndex != -1) {
                // プレースホルダー部分を置き換え
                spannableString.replace(startIndex, startIndex + placeholder.length, value)

                // 置き換えた部分にスタイルを適用
                spannableString.setSpan(
                    StyleSpan(placeholderFont.style),
                    startIndex,
                    startIndex + value.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // 全体のフォントスタイルを適用
        spannableString.setSpan(
            StyleSpan(font.style),
            0,
            spannableString.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        return spannableString
    }
}