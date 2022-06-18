package com.example.lugares.ui.lugar

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lugares.R
import com.example.lugares.databinding.FragmentUpdateLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var lugarViewModel: LugarViewModel

    private val args by navArgs<UpdateLugarFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etWeb.setText(args.lugar.web)

        binding.btAdd.setOnClickListener{ updateLugar() }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Se es eliminar
        if (item.itemId == R.id.menu_delete){
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()

        if (validos(nombre, correo, telefono, web)) {
            val lugar= Lugar(args.lugar.id,nombre,correo,telefono,web, 0.0, 0.0, 0.0, "", "")
            lugarViewModel.updateLugar(lugar)
            Toast.makeText(requireContext(),getString(R.string.msgLugarActualizado),Toast.LENGTH_LONG,).show()

            findNavController().navigate(R.id.action_updateLugarFragment3_to_nav_lugar)
        } else {
            Toast.makeText(requireContext(),getString(R.string.msgFaltanDatos),Toast.LENGTH_LONG,).show()
        }
    }

    private fun deleteLugar() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(getString(R.string.si)) { _,_ ->
            lugarViewModel.deleteLugar(args.lugar)

            Toast.makeText(requireContext(),getString(R.string.msgDeleted) + " ${args.lugar.nombre}",Toast.LENGTH_LONG,).show()

            findNavController().navigate(R.id.action_updateLugarFragment3_to_nav_lugar)
        }

        builder.setNegativeButton(getString(R.string.no)) { _,_ -> }
        builder.setTitle(R.string.msgDeleted)
        builder.setMessage(getString(R.string.confirmarEliminar)+ " ${args.lugar.nombre}?")
        builder.create().show()
    }

    private fun validos(nombre: String, correo: String, telefono: String, web: String): Boolean {
        return !(nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || web.isEmpty())
    }
}