package com.example.lugares.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LugarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is lugar Fragment"
    }
    val text: LiveData<String> = _text
}