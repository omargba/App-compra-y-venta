package com.example.appcomprayventa.Anuncios

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcomprayventa.Adaptadores.AdaptadorImagenSeleccionada
import com.example.appcomprayventa.Constantes
import com.example.appcomprayventa.MainActivity
import com.example.appcomprayventa.Modelo.ModeloImagenSeleccionada
import com.example.appcomprayventa.R
import com.example.appcomprayventa.databinding.ActivityCrearAnuncioBinding
import com.example.appcomprayventa.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CrearAnuncio : AppCompatActivity() {
    private lateinit var binding: ActivityCrearAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imageUri: Uri? = null

    private lateinit var imagenSelecArrayList: ArrayList<ModeloImagenSeleccionada>
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
        cargarImagenes()


        binding.agregarImg.setOnClickListener {
            mostrarOpciones()
        }

        binding.BtnCrearAnuncio.setOnClickListener {
            validarDatos()
        }
    }

    private var marca = ""
    private var categoria = ""
    private var condicion = ""
    private var direccion = ""
    private var precio = ""
    private var titulo = ""
    private var descripcion = ""
    private var latitud = 0.0
    private var longitud = 0.0
    private var Edicion = false

    private fun validarDatos(){
        marca = binding.EtMarca.text.toString().trim()
        categoria = binding.Categoria.text.toString().trim()
        condicion = binding.Condicion.text.toString().trim()
        //direccion = binding.Locacion.text.toString().trim()
        precio = binding.EtPrecio.text.toString().trim()
        titulo = binding.EtTitulo.text.toString().trim()
        descripcion = binding.EtDescripcion.text.toString().trim()

        if (marca.isEmpty()){
            binding.EtMarca.error = "Ingrese la marca"
            binding.EtMarca.requestFocus()
        }
        else if (categoria.isEmpty()){
            binding.Categoria.error = "Ingrese la categoria"
            binding.Categoria.requestFocus()
        }
        else if (condicion.isEmpty()){
            binding.Condicion.error = "Ingrese la condicion"
            binding.Condicion.requestFocus()
        }
        else if (precio.isEmpty()){
            binding.EtPrecio.error = "Ingrese el precio"
            binding.EtPrecio.requestFocus()
        }
        else if (titulo.isEmpty()){
            binding.EtTitulo.error = "Ingrese el titulo"
            binding.EtTitulo.requestFocus()
        }
        else if (descripcion.isEmpty()){
            binding.EtDescripcion.error = "Ingrese la descripcion"
            binding.EtDescripcion.requestFocus()
        }
        else if (imageUri == null) {
            Toast.makeText(
                this,
                "Seleccione al menos una imagen",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            agregarAnuncio()
        }
    }

    private fun agregarAnuncio() {
        progressDialog.setMessage("Agregando anuncio")
        progressDialog.show()

        val tiempo = "${Constantes.obtenerTiempoDis()}"
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["marca"] = "${marca}"
        hashMap["categoria"] = "${categoria}"
        hashMap["condicion"] = "${condicion}"
        hashMap["direccion"] = "${direccion}"
        hashMap["precio"] = "${precio}"
        hashMap["titulo"] = "${titulo}"
        hashMap["descripcion"] = "${descripcion}"
        hashMap["estado"] = "${Constantes.anuncio_disponible}"
        hashMap["tiempo"] = tiempo
        hashMap["latitud"] = latitud
        hashMap["longitud"] = longitud
        hashMap["contadorVistas"] = 0

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                cargarImagenesStorage(keyId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun cargarImagenesStorage(keyId : String) {
        for (i in imagenSelecArrayList.indices){
            val modeloImagenSel = imagenSelecArrayList[i]

            if (!modeloImagenSel.deInternet){
                val nombreImagen = modeloImagenSel.id
                val rutaNombreImagen = "Anuncios/$nombreImagen"

                val storageReference = FirebaseStorage.getInstance().getReference(rutaNombreImagen)
                storageReference.putFile(modeloImagenSel.imagenUri!!)
                    .addOnSuccessListener {taskSnaphot->
                        val uriTask = taskSnaphot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val urlImgCargada = uriTask.result

                        if (uriTask.isSuccessful){
                            val hashMap = HashMap<String, Any>()
                            hashMap["id"] = "${modeloImagenSel.id}"
                            hashMap["imagenUrl"] = "$urlImgCargada"

                            val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
                            ref.child(keyId).child("Imagenes")
                                .child(nombreImagen)
                                .updateChildren(hashMap)
                        }

                        if (Edicion){
                            progressDialog.dismiss()
                            val intent = Intent(this@CrearAnuncio, MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Se actualizó la información del anuncio",
                                Toast.LENGTH_SHORT).show()
                            finishAffinity()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, "Se publicó su anuncio", Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                    }
                    .addOnFailureListener {e->
                        Toast.makeText(
                            this, "${e.message}",Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun limpiarCampos() {
        binding.EtMarca.setText("")
        binding.Categoria.setText("")
        binding.Condicion.setText("")
        //binding.Locacion.setText("")
        binding.EtPrecio.setText("")
        binding.EtTitulo.setText("")
        binding.EtDescripcion.setText("")
        imagenSelecArrayList.clear()
        cargarImagenes()
        imageUri = null
    }




    private fun mostrarOpciones() {
        val popupMenu = PopupMenu(this, binding.agregarImg)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galeria")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId

            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    solicitarPermisosCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    solicitarPermisosCamara.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            } else if (itemId == 2) {
                //Funcionalidad para la Galeria
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imagenGaleria()
                } else {
                    concederPermisosAlmacenamiento.launch(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
            true
        }
    }

    private val solicitarPermisosCamara =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultado ->
            var concedidoTodos = true
            for (seConcede in resultado.values) {
                concedidoTodos = concedidoTodos && seConcede
            }

            if (concedidoTodos) {
                imagenCamara()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de la cámara o almacenamiento se denegaron",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val concederPermisosAlmacenamiento =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { esConcedido ->
            if (esConcedido) {
                imagenGaleria()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de almacenamiento se denegó",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun cargarImagenes(){
        adaptadorImagenSeleccionada = AdaptadorImagenSeleccionada(this, imagenSelecArrayList)
        binding.RVImagenes.adapter = adaptadorImagenSeleccionada
    }

    private val resultadoCamara_ARL =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImagenSel = ModeloImagenSeleccionada(
                    tiempo, imageUri, null, false
                )
                imagenSelecArrayList.add(modeloImagenSel)
                cargarImagenes()
            } else {
                Toast.makeText(
                    this,
                    "La captura de imagen se canceló",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



    private fun imagenCamara() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultadoCamara_ARL.launch(intent)
    }

    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val data = resultado.data
                imageUri = data!!.data
                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImagenSel = ModeloImagenSeleccionada(
                    tiempo, imageUri, null, false
                )
                imagenSelecArrayList.add(modeloImagenSel)
                cargarImagenes()
            } else {
                Toast.makeText(
                    this,
                    "La selección de imagen se canceló",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private fun imagenGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)

        }
}