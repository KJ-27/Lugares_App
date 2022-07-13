package com.example.lugares.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.lugares.data.LugarDao
import com.example.lugares.model.Lugar
import com.example.lugares.repository.LugarRespository

class LugarViewModel(application: Application) : AndroidViewModel(application) {
    val getAllData: MutableLiveData<List<Lugar>>

    private val repository: LugarRespository = LugarRespository(LugarDao())

    init {
        getAllData = repository.getAllData
    }

    fun addLugar (lugar: Lugar) {
        repository.addLugar(lugar)
    }

    fun updateLugar (lugar: Lugar) {
        repository.updateLugar(lugar)
    }

    fun deleteLugar (lugar: Lugar) {
        repository.deleteLugar(lugar)
    }
}