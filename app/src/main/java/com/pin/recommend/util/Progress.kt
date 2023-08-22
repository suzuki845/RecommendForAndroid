package com.pin.recommend.util

class Progress(
    private val onStart: () -> Void,
    private val onComplete: () -> Void,
    private val onError: (Exception) -> Void
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