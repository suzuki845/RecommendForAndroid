package com.pin.recommend.domain.value

sealed class Value<out T> {
    object Absent : Value<Nothing>() // 無視された場合
    data class Present<T>(val value: T) : Value<T>() // 明示的に値が設定された場合
}