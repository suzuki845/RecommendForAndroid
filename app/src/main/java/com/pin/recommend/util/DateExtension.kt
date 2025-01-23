package com.pin.recommend.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Dateに年月日フォーマットの文字列を返す拡張関数
fun Date.toFormattedString(): String {
    val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return format.format(this)
}

fun Date.toMdString(): String {
    val format = SimpleDateFormat("M/d", Locale.getDefault())
    return format.format(this)
}