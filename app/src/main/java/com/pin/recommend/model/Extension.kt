package com.pin.recommend.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun <T> LiveData<T>.awaitValue(): T = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                if (t != null) {
                    removeObserver(this)
                    cont.resume(t)
                }
            }
        }
        observeForever(observer)
        cont.invokeOnCancellation { removeObserver(observer) }
    }
}
