package com.example.lugares.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lugar (
    var id: String,
    val nombre: String,
    val correo: String?,
    val telefono: String?,
    val web: String?,
    val latitud: Double?,
    val longitud: Double?,
    val altura: Double?,
    val rutaAudio: String?,
    val rutaImagen: String?,
) : Parcelable {
    constructor(): this("", "", "", "", "", 0.0, 0.0, 0.0, "", "")
}