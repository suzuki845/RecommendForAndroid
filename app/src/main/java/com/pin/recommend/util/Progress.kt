package com.pin.recommend.util

class Progress(
    private val onStart: () -> Unit,
    private val onComplete: () -> Unit,
    private val onError: (Exception) -> Unit
){
    fun onStart(){
        onStart.invoke()
    }

    fun onComplete(){
        onComplete.invoke()
    }

    fun onError(e: Exception){
        onError.invoke(e)
    }
}