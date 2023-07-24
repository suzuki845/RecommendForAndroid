package com.pin.recommend.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T0, T1, R> combine2(
    a: LiveData<T0>,
    b: LiveData<T1>,
    block: (T0?, T1?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(a) {
        result.value = block(a.value, b.value)
    }
    result.addSource(b) {
        result.value = block(a.value, b.value)
    }
    return result
}

fun <T0, T1, T2, R> combine3(
    a: LiveData<T0>,
    b: LiveData<T1>,
    c: LiveData<T2>,
    block: (T0?, T1?, T2?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(a) {
        result.value = block(a.value, b.value, c.value)
    }
    result.addSource(b) {
        result.value = block(a.value, b.value, c.value)
    }
    result.addSource(c) {
        result.value = block(a.value, b.value, c.value)
    }
    return result
}

fun <T0, T1, T2, T3, R> combine4(
    a: LiveData<T0>,
    b: LiveData<T1>,
    c: LiveData<T2>,
    d: LiveData<T3>,
    block: (T0?, T1?, T2?, T3?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(a) {
        result.value = block(a.value, b.value, c.value, d.value)
    }
    result.addSource(b) {
        result.value = block(a.value, b.value, c.value, d.value)
    }
    result.addSource(c) {
        result.value = block(a.value, b.value, c.value, d.value)
    }
    result.addSource(d) {
        result.value = block(a.value, b.value, c.value, d.value)
    }
    return result
}

fun <T0, T1, T2, T3, T4, R> combine5(
    a: LiveData<T0>,
    b: LiveData<T1>,
    c: LiveData<T2>,
    d: LiveData<T3>,
    e: LiveData<T4>,
    block: (T0?, T1?, T2?, T3?, T4?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(a) {
        result.value = block(a.value, b.value, c.value, d.value, e.value)
    }
    result.addSource(b) {
        result.value = block(a.value, b.value, c.value, d.value, e.value)
    }
    result.addSource(c) {
        result.value = block(a.value, b.value, c.value, d.value, e.value)
    }
    result.addSource(d) {
        result.value = block(a.value, b.value, c.value, d.value, e.value)
    }
    result.addSource(e) {
        result.value = block(a.value, b.value, c.value, d.value, e.value)
    }
    return result
}