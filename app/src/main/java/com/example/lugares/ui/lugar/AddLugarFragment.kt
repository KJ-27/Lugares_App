package com.example.lugares.ui.lugar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lugares.R
import com.example.lugares.databinding.FragmentAddLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles

class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var lugarViewModel: LugarViewModel

    private lateinit var audioUtiles: AudioUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>
    private lateinit var imagenUtiles: ImagenUtiles

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.btAdd.setOnClickListener{
            if (binding.etNombre.text.toString().isNotEmpty()) {
                binding.progressBar.visibility = ProgressBar.VISIBLE
                binding.msgMensaje.text = "Subiendo nota de audio"
                binding.msgMensaje.visibility = TextView.VISIBLE
                subeAudioNube()
            } else {
                Toast.makeText(requireContext(),"Faltan Datos",Toast.LENGTH_LONG).show()
            }
        }

        audioUtiles = AudioUtiles(
            requireActivity(), requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio)
        )

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagenUtiles.actualizaFoto()
            }
        }

        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity
        )

        ubicaGPS()

        return binding.root
    }

    private fun subeAudioNube() {
        val audioFile = audioUtiles.audioFile
        if (audioFile.exists() && audioFile.isFile && audioFile.canRead()) {
            val ruta = Uri.fromFile(audioFile)
            var reference: StorageReference = Firebase.storage.reference.child(
                "lugaresApp/${Firebase.auth.currentUser?.uid}/audios/${audioFile.name}}"
            )

            val uploadTask = reference.putFile(ruta)
            uploadTask
                .addOnSuccessListener{
                    val downloadUrl = reference.downloadUrl
                    downloadUrl.addOnSuccessListener{
                        val rutaNota = it.toString()
                        subeImagenNube(rutaNota)
                    }
                }

            uploadTask
                .addOnFailureListener{
                    Toast.makeText(context, "Error subiendo nota", Toast.LENGTH_LONG).show()
                    subeImagenNube("")
                }
        } else {
            Toast.makeText(context, "Error subiendo nota", Toast.LENGTH_LONG).show()
            subeImagenNube("")
        }
    }

    private fun subeImagenNube(rutaAudio: String) {
        binding.msgMensaje.text = "Subiendo imagen..."
        val imageFile = imagenUtiles.imagenFile

        if (imagenUtiles.isImagenFileInitialized() && imageFile.exists() && imageFile.isFile && imageFile.canRead()) {
            val ruta = Uri.fromFile(imageFile)
            var reference: StorageReference = Firebase.storage.reference.child(
                "lugaresApp/${Firebase.auth.currentUser?.uid}/audios/${imageFile.name}}"
            )

            val uploadTask = reference.putFile(ruta)
            uploadTask
                .addOnSuccessListener{
                    val downloadUrl = reference.downloadUrl
                    downloadUrl.addOnSuccessListener{
                        val rutaImagen = it.toString()
                        insertarLugar(rutaAudio, rutaImagen)
                    }
                }

            uploadTask
                .addOnFailureListener{
                    Toast.makeText(context, "Error subiendo nota", Toast.LENGTH_LONG).show()
                    subeImagenNube("")
                }
        } else {
            Toast.makeText(context, "Error subiendo nota", Toast.LENGTH_LONG).show()
            subeImagenNube("")
        }
    }

    private fun ubicaGPS() {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val conPermisos = true

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 105)

        }

        if (conPermisos) { fusedLocationClient.lastLocation.addOnSuccessListener{ location: Location? ->
                if (location != null) {
                    binding.tvLatitud2.text = "${location.latitude}"
                    binding.tvLongitud2.text ="${location.longitude}"
                    binding.tvAltura2.text = "${location.altitude}"
                } else {
                    binding.tvLatitud2.text = getString(R.string.error)
                    binding.tvLongitud2.text = getString(R.string.error)
                    binding.tvAltura2.text = getString(R.string.error)
                }
            }
        }
    }

    private fun insertarLugar(rutaAudio: String, rutaImagen: String) {
        binding.msgMensaje.text = "Subiendo Lugar..."
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val altitud = binding.tvAltura2.text.toString().toDouble()
        val latitud = binding.tvLatitud2.text.toString().toDouble()
        val longitud = binding.tvLongitud2.text.toString().toDouble()

        if (validos(nombre, correo, telefono, web)) {
            val lugar= Lugar("",nombre,correo,telefono,web, latitud, longitud, altitud, rutaAudio, rutaImagen)
            lugarViewModel.addLugar(lugar)
            binding.progressBar.visibility = ProgressBar.GONE

            Toast.makeText(requireContext(),getString(R.string.msgLugarAgregado),Toast.LENGTH_LONG,).show()

            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar3)
        } else {
            Toast.makeText(requireContext(),getString(R.string.msgFaltanDatos),Toast.LENGTH_LONG,).show()
        }
    }

    private fun validos(nombre: String, correo: String, telefono: String, web: String): Boolean {
        return !(nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || web.isEmpty())
    }
}