package com.example.lugares.repository

import androidx.lifecycle.LiveData
import com.example.lugares.data.LugarDao
import com.example.lugares.model.Lugar

class LugarRespository(private val lugarDao: LugarDao) {
    val getAllData: LiveData<List<Lugar>> = lugarDao.getAllData()

    suspend fun addLugar(lugar: Lugar){
        lugarDao.addLugar(lugar)
    }

    suspend fun updateLugar(lugar: Lugar){
        lugarDao.updateLugar(lugar)
    }

    suspend fun deleteLugar(lugar: Lugar){
        lugarDao.deleteLugar(lugar)
    }
}