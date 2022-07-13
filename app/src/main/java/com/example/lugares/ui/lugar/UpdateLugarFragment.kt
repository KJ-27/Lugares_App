package com.example.lugares.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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

        //Textos para editar
        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etWeb.setText(args.lugar.web)

        //Text View de Altura/Latitud/Longitud
        binding.tvAltura.text = args.lugar.altura.toString()
        binding.tvLatitud.text = args.lugar.latitud.toString()
        binding.tvLongitud.text = args.lugar.longitud.toString()

        //Disparan las acciones de su determinado botón
        binding.btnPhone.setOnClickListener { llamarLugar() }
        binding.btnEmail.setOnClickListener { escribirCorreo() }
        binding.btnWhatsApp.setOnClickListener { enviarWhatsApp() }
        binding.btnWeb.setOnClickListener { verWebLugar() }
        binding.btnLocation.setOnClickListener { verMapa() }

        //Dispara el update
        binding.btAdd.setOnClickListener{ updateLugar() }

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun llamarLugar() {
        val telefono = binding.etTelefono.text

        if (telefono.isNotEmpty()) {
            val dialIntent = Intent(Intent.ACTION_CALL)
            dialIntent.data = Uri.parse("tel:$telefono")

            if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 105)
            } else {
                requireActivity().startActivity(dialIntent)
            }

        }else{
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }

    private fun escribirCorreo() {
        val para = binding.etCorreo.text.toString()

        if (para.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(para))
            intent.putExtra(
                Intent.EXTRA_SUBJECT, getString(R.string.msg_saludos) + " " + binding.etNombre.text
            )
            intent.putExtra(
                Intent.EXTRA_TEXT, getString(R.string.msg_mensaje_correo)
            )
            startActivity(intent)
        } else{
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarWhatsApp() {
        val telefono = binding.etTelefono.text

        if (telefono.isNotEmpty()) {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$telefono&text="+getString(R.string.msg_saludos)

            sendIntent.setPackage("com.whatsapp")
            sendIntent.data = Uri.parse(uri)
            startActivity(sendIntent)

        }else{
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }

    private fun verWebLugar() {
        val sitio = binding.etWeb.text.toString()

        if (sitio.isNotEmpty()) {
            val webPage = Uri.parse("https://$sitio")
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            startActivity(intent)
        }else{
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }

    private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()

        if (latitud.isFinite() && longitud.isFinite()) {
            val location = Uri.parse("geo:$latitud,$longitud?z=18")
            val mapIntent = Intent(Intent.ACTION_VIEW, location)
            startActivity(mapIntent)
        }else{
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
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