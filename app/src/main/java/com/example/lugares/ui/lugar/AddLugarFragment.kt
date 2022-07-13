package com.example.lugares.ui.lugar

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lugares.R
import com.example.lugares.databinding.FragmentAddLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var lugarViewModel: LugarViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.btAdd.setOnClickListener{ insertarLugar() }

        ubicaGPS()

        return binding.root
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

    private fun insertarLugar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val altitud = binding.tvAltura2.text.toString().toDouble()
        val latitud = binding.tvLatitud2.text.toString().toDouble()
        val longitud = binding.tvLongitud2.text.toString().toDouble()

        if (validos(nombre, correo, telefono, web)) {
            val lugar= Lugar(0,nombre,correo,telefono,web, latitud, longitud, altitud, "", "")
            lugarViewModel.addLugar(lugar)
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