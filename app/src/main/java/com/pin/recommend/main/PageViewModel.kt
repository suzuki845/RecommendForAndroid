package com.pin.recommend.main

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class PageViewModel : ViewModel() {
    private val mIndex = MutableLiveData<Int>()
    val text: LiveData<String> =
        mIndex.map{ input -> "Hello world from section: $input" }

    fun setIndex(index: Int) {
        mIndex.value = index
    }
}