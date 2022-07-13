package com.example.lugares.repository

import androidx.lifecycle.MutableLiveData
import com.example.lugares.data.LugarDao
import com.example.lugares.model.Lugar

class LugarRespository(private val lugarDao: LugarDao) {
    val getAllData: MutableLiveData<List<Lugar>> = lugarDao.getAllData()

    fun addLugar(lugar: Lugar){
        lugarDao.addLugar(lugar)
    }

    fun updateLugar(lugar: Lugar){
        lugarDao.updateLugar(lugar)
    }

    fun deleteLugar(lugar: Lugar){
        lugarDao.deleteLugar(lugar)
    }
}