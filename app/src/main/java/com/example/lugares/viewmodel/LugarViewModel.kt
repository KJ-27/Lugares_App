package com.example.lugares.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lugares.data.LugarDatabase
import com.example.lugares.model.Lugar
import com.example.lugares.repository.LugarRespository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LugarViewModel(application: Application) : AndroidViewModel(application) {
    val getAllData: LiveData<List<Lugar>>

    private val repository: LugarRespository

    init {
        val lugarDao = LugarDatabase.getDataBase(application).lugarDao()
        repository = LugarRespository(lugarDao)
        getAllData = repository.getAllData
    }

    fun addLugar (lugar: Lugar) {
        viewModelScope.launch(Dispatchers.IO) { repository.addLugar(lugar) }
    }

    fun updateLugar (lugar: Lugar) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateLugar(lugar) }
    }

    fun deleteLugar (lugar: Lugar) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteLugar(lugar) }
    }
}