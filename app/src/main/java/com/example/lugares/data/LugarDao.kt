package com.example.lugares.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lugares.model.Lugar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

class LugarDao {
    private var codigoUsuario: String
    private var firestore: FirebaseFirestore
    private var lugaresApp = "lugaresApp"
    private var miColeccion = "misLugares"

    init {
        val usuario = Firebase.auth.currentUser?.email
        codigoUsuario = "$usuario"

        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun getAllData() : MutableLiveData<List<Lugar>>{
        val listaLugares = MutableLiveData<List<Lugar>>()

        firestore
            .collection(lugaresApp)
            .document(codigoUsuario)
            .collection(miColeccion)
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = ArrayList<Lugar>()
                    val lugares = snapshot.documents

                    lugares.forEach{
                        val lugar = it.toObject(Lugar::class.java)

                        if (lugar != null){
                            lista.add(lugar)
                        }
                    }

                    listaLugares.value = lista
                }
            }
        return listaLugares
    }

    fun addLugar(lugar : Lugar) {
        var document: DocumentReference

        if (lugar.id.isEmpty()){
            document = firestore
                .collection(lugaresApp)
                .document(codigoUsuario)
                .collection(miColeccion)
                .document()

            lugar.id = document.id
        } else {
            document = firestore
                .collection(lugaresApp)
                .document(codigoUsuario)
                .collection(miColeccion)
                .document(lugar.id)
        }

        val set = document.set(lugar)

        set
            .addOnSuccessListener {
                Log.d("AddLugar", "Lugar Agregado: " + lugar.id)
            }
            .addOnCanceledListener {
                Log.d("AddLugar", "Lugar NO Agregado: "  + lugar.id)
            }
    }

    fun updateLugar(lugar : Lugar) {
        addLugar(lugar)
    }

    fun deleteLugar(lugar : Lugar) {
        if (lugar.id.isNotEmpty()) {
            firestore
                .collection(lugaresApp)
                .document(codigoUsuario)
                .collection(miColeccion)
                .document(lugar.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("DeleteLugar", "Lugar Eliminado: " + lugar.id)
                }
                .addOnCanceledListener {
                    Log.d("DeleteLugar", "Lugar NO Eliminado: "  + lugar.id)
                }
        }
    }
}