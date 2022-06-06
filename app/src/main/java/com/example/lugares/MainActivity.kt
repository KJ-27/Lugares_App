package com.example.lugares

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.lugares.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    //Declaramos variables
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializamos el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        //Definimos el content view
        setContentView(binding.root)

        //Inicializar Firebase
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        //Metodos de los botones

        //Listener al click sobre login
        binding.login.setOnClickListener{
            //Llamada al metodo login
            login()
        }

        //Listener al click sobre register
        binding.register.setOnClickListener{
            //Llamada al metodo register
            register()
        }
    }

    private fun update(user: FirebaseUser?){
        if(user != null){
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        update(user)
    }

    //Metodo login
    private fun login() {
        //Obtener las variables para loging
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                Log.d("User Login", "Success")
                update(user)
            } else {
                Log.d("User Login", "Fail")
                Toast.makeText(baseContext, "Fail", Toast.LENGTH_LONG).show()
                update(null)
            }
        }
    }

    //Metodo register
    private fun register() {
        //Obtener las variables para register
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                Log.d("User Register", "Success")
                update(user)
            } else {
                Log.d("User Register", "Fail")
                Toast.makeText(baseContext, "Fail", Toast.LENGTH_LONG).show()
                update(null)
            }
        }
    }
}