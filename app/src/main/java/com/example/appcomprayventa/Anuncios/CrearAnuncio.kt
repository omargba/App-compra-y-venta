package com.example.appcomprayventa.Anuncios

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcomprayventa.Adaptadores.AdaptadorImagenSeleccionada
import com.example.appcomprayventa.Constantes
import com.example.appcomprayventa.Modelo.ModeloImagenSeleccionada
import com.example.appcomprayventa.R
import com.example.appcomprayventa.databinding.ActivityCrearAnuncioBinding
import com.example.appcomprayventa.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth

class CrearAnuncio : AppCompatActivity() {
    private lateinit var binding: ActivityCrearAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri : Uri?=null

    private lateinit var imagenSelecArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSeleccionada: AdaptadorImagenSeleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)

        val adaptadorCat = ArrayAdapter(
            this,
            R.layout.item_categoria,
            Constantes.categorias
        )
        binding.Categoria.setAdapter(adaptadorCat)

        val adaptadorCon = ArrayAdapter(
            this,
            R.layout.item_condicion,
            Constantes.condiciones
        )
        binding.Condicion.setAdapter(adaptadorCon)


        imagenSelecArrayList = ArrayList()
//        cargarImagenes()

        binding.agregarImg.setOnClickListener {
//            mostarOpciones()
        }

        adaptadorImagenSeleccionada = AdaptadorImagenSeleccionada(this, imagenSelecArrayList)
}
    private fun mostrarOpciones(){
        val popupMenu = PopupMenu(this, binding.agregarImg)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galeria")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1){

            } else{

            }





    }
}