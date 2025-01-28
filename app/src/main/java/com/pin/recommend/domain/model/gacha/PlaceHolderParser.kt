package com.pin.recommend.domain.model.gacha

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

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


    fun parseForComposable(
        // メソッド名を変更
        values: List<String>,
        fontSize: TextUnit = 16.sp, // 全体のフォントサイズ
        fontStyle: FontStyle = FontStyle.Normal, // 全体のフォントスタイル,
        fontWeight: FontWeight = FontWeight.Normal, // 全体のフォントスタイル
        placeholderFontSize: TextUnit = 16.sp, // プレースホルダー部分のフォントサイズ
        placeholderFontStyle: FontStyle = FontStyle.Normal,// プレースホルダー部分のフォントスタイル
        placeholderFontWeight: FontWeight = FontWeight.Bold // プレースホルダー部分のフォントスタイル
    ): AnnotatedString {
        return buildAnnotatedString {
            var remainingTemplate = template

            for ((index, value) in values.withIndex()) {
                val placeholder = "$$index"
                val placeholderIndex = remainingTemplate.indexOf(placeholder)

                if (placeholderIndex != -1) {
                    // プレースホルダーの前のテキストをそのまま追加（全体スタイル）
                    append(
                        AnnotatedString(
                            remainingTemplate.substring(0, placeholderIndex),
                            SpanStyle(
                                fontSize = fontSize,
                                fontStyle = fontStyle,
                                fontWeight = fontWeight
                            )
                        )
                    )

                    // プレースホルダー部分をスタイル適用して追加
                    append(
                        AnnotatedString(
                            value,
                            SpanStyle(
                                fontSize = placeholderFontSize,
                                fontStyle = placeholderFontStyle,
                                fontWeight = placeholderFontWeight
                            )
                        )
                    )

                    // 処理済み部分を削除して更新
                    remainingTemplate =
                        remainingTemplate.substring(placeholderIndex + placeholder.length)
                }
            }

            // 残りのテキストを追加
            append(
                AnnotatedString(
                    remainingTemplate,
                    SpanStyle(fontSize = fontSize)
                )
            )
        }
    }

}